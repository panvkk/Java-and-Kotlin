# Refresh-токен переехал в HttpOnly cookie

Дата: 2026-11-16
Затронуто: `auth-service` (контроллеры), фронтенд `money-pusher/frontend`.
`api-gateway` менять не нужно: он прозрачно проксирует `Cookie` и `Set-Cookie`.

## 1. Коротко

- `refresh_token` больше **не возвращается в JSON**.
- Refresh-токен приходит в `Set-Cookie` при `register`, `authenticate` и `refresh-token`.
- Cookie: имя `refresh_token`, `HttpOnly`, `Secure`, `SameSite=Lax`.
- `POST /api/v1/auth/refresh-token` больше не принимает
  `Authorization: Bearer <refresh_token>`. Токен читается из cookie.
- `POST /api/v1/logout` больше не принимает `refresh_token` в теле.
  В теле остаётся только `access_token`, refresh читается из cookie.
- Access-токен по-прежнему передаётся в `Authorization: Bearer <access_token>`.

## 2. Контракты эндпоинтов

### POST /api/v1/auth/register

Запрос:

```json
{ "nickname": "demo", "email": "demo@example.com", "password": "password123" }
```

Ответ `200`:

```json
{ "access_token": "eyJ..." }
```

Заголовок ответа:

```http
Set-Cookie: refresh_token=<jwt>; Path=/api/v1; Max-Age=...; HttpOnly; Secure; SameSite=Lax
```

### POST /api/v1/auth/authenticate

Запрос:

```json
{ "email": "demo@example.com", "password": "password123" }
```

Ответ `200`: только `{ "access_token": "..." }` + `Set-Cookie` с refresh-токеном.

`401` — неверный email/пароль.

### POST /api/v1/auth/refresh-token

Запрос:

- тела нет;
- `Authorization` не нужен;
- cookie `refresh_token` браузер отправляет сам.

Ответ `200`:

```json
{ "access_token": "eyJ..." }
```

Плюс `Set-Cookie` с **новым** refresh-токеном (ротация: старый инвалидируется).

`401` — cookie отсутствует, истекла или уже использована.

### POST /api/v1/logout

Заголовки:

```http
Authorization: Bearer <access_token>
Cookie: refresh_token=<jwt>
```

Тело:

```json
{ "access_token": "eyJ..." }
```

Ответ `200` — пустое тело, плюс `Set-Cookie`, который удаляет refresh-cookie
(`Max-Age=0`).

`400` — refresh-cookie не пришла.
`401` — access-токен невалиден/истёк.

## 3. Что поменять во фронтенде

### 3.1. Тип токенов

`frontend/src/types.ts`:

```ts
export type AuthTokens = { access_token: string };
```

### 3.2. Хранение

Refresh-токен **нельзя** хранить в `sessionStorage`/`localStorage`: он `HttpOnly`,
JS его не видит. Храним только access-токен.

`frontend/src/lib/api.ts`, было:

```ts
const ACCESS_KEY = 'asphalt-clicker-access';
const REFRESH_KEY = 'asphalt-clicker-refresh';

let tokens: AuthTokens | null = (() => {
  const accessToken = sessionStorage.getItem(ACCESS_KEY);
  const refreshToken = sessionStorage.getItem(REFRESH_KEY);
  return accessToken && refreshToken
    ? { access_token: accessToken, refresh_token: refreshToken }
    : null;
})();

export function saveTokens(next: AuthTokens) {
  tokens = next;
  sessionStorage.setItem(ACCESS_KEY, next.access_token);
  sessionStorage.setItem(REFRESH_KEY, next.refresh_token);
}
```

стало:

```ts
const ACCESS_KEY = 'asphalt-clicker-access';
const LEGACY_REFRESH_KEY = 'asphalt-clicker-refresh';

// Убираем refresh-токен, который мог остаться от старой версии.
sessionStorage.removeItem(LEGACY_REFRESH_KEY);

let tokens: AuthTokens | null = (() => {
  const accessToken = sessionStorage.getItem(ACCESS_KEY);
  return accessToken ? { access_token: accessToken } : null;
})();

export function saveTokens(next: AuthTokens) {
  tokens = next;
  sessionStorage.setItem(ACCESS_KEY, next.access_token);
}

export function clearTokens() {
  tokens = null;
  sessionStorage.removeItem(ACCESS_KEY);
  sessionStorage.removeItem(LEGACY_REFRESH_KEY);
}
```

### 3.3. Refresh

Было:

```ts
const refreshToken = tokens?.refresh_token;
fetch(`${baseUrl}/api/v1/auth/refresh-token`, {
  method: 'POST',
  headers: { Authorization: `Bearer ${refreshToken}` },
});
```

Стало:

```ts
async function refresh(): Promise<AuthTokens> {
  if (refreshFlight) return refreshFlight;
  refreshFlight = fetch(`${baseUrl}/api/v1/auth/refresh-token`, {
    method: 'POST',
    credentials: 'include',
  })
    .then(async (response) => {
      if (!response.ok) throw await parseError(response);
      const next = (await response.json()) as AuthTokens;
      saveTokens(next);
      return next;
    })
    .finally(() => {
      refreshFlight = null;
    });
  return refreshFlight;
}
```

Важно: `credentials: 'include'` обязателен, если фронт и API находятся на разных
origin (например, `localhost:5500` → `localhost:8080`). Для same-origin
(`https://clicker.asphalt.by` → `/api/v1/...`) браузер отправит cookie и так,
но `include` безопаснее и одинаково работает в обоих случаях.

### 3.4. Условие авто-refresh

Было:

```ts
if (response.status === 401 && canRefresh && tokens?.refresh_token) {
```

Стало (refresh-токена в JS больше нет, ориентируемся на наличие сессии):

```ts
if (response.status === 401 && canRefresh && tokens?.access_token) {
```

### 3.5. Все запросы

В `fetch` внутри `request` добавить `credentials`:

```ts
const response = await fetch(`${baseUrl}${path}`, {
  ...init,
  headers,
  credentials: 'include',
});
```

Это нужно, чтобы браузер сохранял `Set-Cookie` от login/register и отправлял
cookie на refresh/logout.

### 3.6. Logout

Было:

```ts
logout: () => request<void>('/api/v1/logout', {
  method: 'POST',
  body: JSON.stringify({
    access_token: tokens?.access_token || '',
    refresh_token: tokens?.refresh_token || '',
  }),
}, false),
```

Стало:

```ts
logout: () => request<void>('/api/v1/logout', {
  method: 'POST',
  body: JSON.stringify({ access_token: tokens?.access_token || '' }),
}, false),
```

`Layout.tsx` уже правильно чистит локальную сессию даже при ошибке logout — это
поведение нужно сохранить.

### 3.7. Восстановление сессии в новой вкладке (опционально)

`sessionStorage` у каждой вкладки свой, а refresh-cookie общая. Если хочется,
чтобы новая вкладка не показывала логин при живой cookie, на старте приложения
можно попробовать один refresh:

```ts
export async function restoreSession(): Promise<boolean> {
  if (tokens?.access_token) return true;
  try {
    const next = await refresh();
    return Boolean(next.access_token);
  } catch {
    return false;
  }
}
```

и в `App.tsx` — асинхронная инициализация `authenticated`.
Если такое поведение не нужно, шаг можно пропустить.

### 3.8. Очистка старого хранилища

Раньше токены лежали в `localStorage` под `asphalt-clicker-auth`, теперь —
в `sessionStorage`. При загрузке приложения старые ключи нужно удалить:

```ts
localStorage.removeItem('asphalt-clicker-auth');
sessionStorage.removeItem('asphalt-clicker-refresh');
```

## 4. Чего фронтенд делать не должен

- Не читать `refresh_token` из ответа JSON — его там больше нет.
- Не отправлять `refresh_token` в теле logout.
- Не отправлять refresh-токен в `Authorization` на `/refresh-token`.
- Не пытаться прочитать/удалить refresh-cookie через `document.cookie`: она
  `HttpOnly`. Удаление — только через `POST /api/v1/logout`.
- Не делать параллельные refresh из разных вкладок: токен одноразовый
  (ротация), второй запрос получит `401`. Внутри одной вкладки `refreshFlight`
  уже склеивает параллельные вызовы.

## 5. CORS и локальная разработка

- Production: фронт и API на одном origin (`https://clicker.asphalt.by`),
  CORS фактически не участвует.
- Если фронт на `http://localhost:5500`, а API на `http://localhost:8080`:
  нужен `credentials: 'include'`, а gateway уже отдаёт
  `Access-Control-Allow-Credentials: true` и разрешает `http://localhost:5500`.
- Cookie помечена `Secure`; современные браузеры разрешают такие cookie на
  `localhost`, но при проблемах в dev проще поднять HTTPS или сделать флаг
  `secure` конфигурируемым.

## 6. Критерии приёмки

1. `POST /authenticate` → в Response JSON только `access_token`, в
   DevTools → Application → Cookies появилась `refresh_token` (`HttpOnly`,
   `Secure`, `SameSite=Lax`).
2. `POST /refresh-token` → уходит без `Authorization`, браузер сам отправляет
   cookie; ответ `200` с новым `access_token` и обновлённой cookie.
3. `POST /logout` → уходит с `Authorization` и cookie, отвечает `200`,
   refresh-cookie исчезает; повторный `refresh-token` возвращает `401`.
4. В `sessionStorage`/`localStorage` нет `refresh_token`.
5. После истечения access-токена (≈15 минут) приложение само обновляет его и
   повторяет запрос; при неудаче показывает логин.

## 7. Бэкенд: что уже исправлено

Эти три бага были найдены при ревью и уже поправлены в `auth-service`.
Фронтенду ничего делать не нужно, раздел оставлен для контекста.

1. **`refresh-token` теперь отдаёт cookie заголовком `Set-Cookie`.**
   Было `.header(REFRESH_TOKEN_COOKIE, ...)`, стало
   `.header(HttpHeaders.SET_COOKIE, ...)`.
   Это важно: при старом варианте после первого refresh в браузере оставался
   старый refresh-токен, а сервер его уже инвалидировал → следующий refresh
   возвращал `401`.

2. **Cookie-path расширен до `/api/v1`.**
   `LogoutController` живёт на `/api/v1/logout`, поэтому при `Path=/api/v1/auth`
   браузер не отправил бы туда refresh-cookie и logout вернул бы `400`.
   Сейчас и установка, и удаление cookie используют `.path("/api/v1")`.

3. **`maxAge` считается из миллисекунд.**
   `refreshTokenExpiration` = `604800000` мс, а `ResponseCookie.maxAge(long)`
   принимает секунды. Теперь используется
   `.maxAge(Duration.ofMillis(jwtProperties.refreshTokenExpiration()))`.

Дополнительно стоит решить, что делать с logout при истёкшем access-токене:
сейчас gateway вернёт `401`, и cookie останется живой. Варианты: разрешить
logout только по refresh-cookie (доставать email из неё) или на фронте перед
logout делать refresh.
