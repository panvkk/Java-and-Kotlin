# Money Pusher — API v1

Документ для команды фронтенда. Описывает публичный HTTP API, доступный через `api-gateway`.

- **Base URL (локально):** `http://localhost:8080`
- **Base URL (production):** `https://clicker.asphalt.by` — фронт и API на одном origin
- **Формат:** JSON, `Content-Type: application/json`
- **Авторизация:** `Authorization: Bearer <access_token>`; refresh-токен — только в
  HttpOnly cookie `refresh_token` (из JavaScript недоступен)
- **Деньги:** целые числа (`Long`), единица — «coin». Дробных значений нет.
- **Машиночитаемая спека:** [`openapi.yaml`](./openapi.yaml) — импортируется в Postman
  (Import → File), Swagger Editor или Redoc.

> Все запросы идут **только через gateway** (`:8080`). Не обращайтесь напрямую к `auth-service`
> (`:8081`), `clicker-service` (`:8082`) и `transfer-service` (`:8083`) — в Docker они вообще
> не опубликованы наружу.

---

## Содержание

1. [Быстрый старт](#быстрый-старт)
2. [Общие правила](#общие-правила)
3. [Аутентификация](#аутентификация)
4. [Эндпоинты](#эндпоинты)
   - [Auth](#auth)
   - [Users](#users)
   - [Clicker](#clicker)
   - [Leaderboard](#leaderboard)
   - [Transfers](#transfers)
5. [Асинхронная модель](#асинхронная-модель)
6. [Ошибки](#ошибки)
7. [Лимиты и CORS](#лимиты-и-cors)
8. [Модели данных](#модели-данных)
9. [Чек-лист интеграции](#чек-лист-интеграции)

---

## Быстрый старт

```bash
BASE=http://localhost:8080
JAR=cookies.txt   # для локального curl: refresh-токен живёт в cookie

# 1. Регистрация: access_token в JSON, refresh-токен — в Set-Cookie
curl -s -c "$JAR" -X POST "$BASE/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"nickname":"demo","email":"demo@example.com","password":"password123"}'

# 2. Профиль (публичный эндпоинт)
curl -s "$BASE/api/v1/users/demo@example.com"

# 3. Баланс (нужен access_token)
curl -s "$BASE/api/v1/clicker/balance/demo@example.com" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# 4. Клик
curl -s -X PATCH "$BASE/api/v1/clicker" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{"balance_increase":1}'

# 5. Перевод (202 Accepted, обработка асинхронная)
curl -s -X POST "$BASE/api/v1/transfers" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d '{"target_user_email":"friend@example.com","transfer_amount":10,"additional_message":"gg"}'

# 6. История переводов / статусы
curl -s "$BASE/api/v1/transfers" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

# 7. Обновление access_token: без тела и без Authorization, только cookie
curl -s -b "$JAR" -c "$JAR" -X POST "$BASE/api/v1/auth/refresh-token"

# 8. Logout: access_token в заголовке и в теле, refresh-токен — из cookie
curl -s -b "$JAR" -c "$JAR" -X POST "$BASE/api/v1/logout" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -d "{\"access_token\":\"$ACCESS_TOKEN\"}"
```

В браузере вместо `-c/-b` используется `credentials: 'include'` во всех `fetch`
(см. раздел [Аутентификация](#аутентификация)).

---

## Общие правила

| Правило | Значение |
|---|---|
| Базовый путь | `/api/v1` |
| Кодировка | UTF-8 |
| Имена полей | `snake_case` |
| Деньги | целое число, `1` = 1 coin |
| Время переводов | ISO-8601 UTC (`2026-11-14T09:15:30.123456Z`) |
| Время в ошибках | локальное время сервера без таймзоны (`2026-11-14T12:34:56.789`) |
| Пустой успешный ответ | тело отсутствует (HTTP 200) |
| Создание перевода | HTTP 202 + тело с `status: "PENDING"` |
| Регистрация/логин | HTTP 200 (не 201) |
| Refresh-токен | HttpOnly cookie `refresh_token`, приходит в `Set-Cookie` |
| Cookie-запросы | `credentials: 'include'` обязателен (`/refresh-token`, `/logout`, login/register) |

Все защищённые эндпоинты требуют **access token**. Gateway сам проверяет подпись, срок жизни,
тип токена (`ACCESS`) и blacklist, после чего прокидывает email пользователя во внутренний
заголовок `X-User-Name`. **Фронтенд не должен отправлять `X-User-Name` вручную.**

---

## Аутентификация

### Схема

1. `POST /api/v1/auth/register` или `POST /api/v1/auth/authenticate` возвращают
   `access_token` в JSON и устанавливают refresh-cookie через `Set-Cookie`.
2. `access_token` передаётся в `Authorization: Bearer ...` для защищённых запросов.
3. `access_token` живёт **15 минут** по умолчанию, refresh-токен — **7 дней**.
4. Перед истечением access token обменяйте refresh-cookie на новый access token через
   `POST /api/v1/auth/refresh-token` (тела и заголовка `Authorization` нет).
5. При logout access token уходит в blacklist, refresh-токен удаляется на сервере,
   cookie очищается в ответе.

### Cookie

Refresh-токен живёт только в cookie и недоступен из JavaScript:

```http
Set-Cookie: refresh_token=<jwt>; Path=/api/v1; Max-Age=<refreshTokenExpiration/1000>; HttpOnly; Secure; SameSite=Lax
```

- `HttpOnly` — `document.cookie` его не видит; хранить токен в `localStorage` или
  `sessionStorage` не нужно и нельзя;
- `Secure` — отправляется только по HTTPS (в проде — `https://clicker.asphalt.by`);
- `SameSite=Lax` — обычные same-origin запросы работают;
- `Path=/api/v1` — покрывает и `/api/v1/auth/refresh-token`, и `/api/v1/logout`.

### Важно про refresh

- Refresh-токен передаётся **не в заголовке**, а в cookie. Тела нет, `Authorization`
  не нужен. В браузере обязателен `credentials: 'include'`.
- `refresh_token` **не приходит в JSON**. Если фронт ждёт поле `refresh_token` в ответе —
  его там больше нет.
- Refresh-токен **не подходит** для защищённых эндпоинтов: gateway пропускает только токены
  с типом `ACCESS`.
- При обновлении refresh-токен **ротируется**: старый сразу становится недействительным,
  в ответ приходит новый `Set-Cookie`. Поэтому два параллельных refresh (например, из двух
  вкладок) приведут к ошибке одного из них. Делайте single-flight: один refresh на всё
  приложение/вкладки.
- Logout требует живой access token: gateway по нему определяет пользователя. Если access
  истёк — сначала refresh, потом logout. Локальную сессию чистите в любом случае.

### Рекомендуемая обработка 401

```js
// Псевдокод
async function authorizedFetch(url, options) {
  let response = await fetch(url, withAuth(options)); // credentials: 'include'

  if (response.status === 401) {
    const refreshed = await refreshOnce(); // single-flight
    if (!refreshed) {
      clearSession();
      location.replace("/login");
      return response;
    }
    response = await fetch(url, withAuth(options)); // один повтор
  }

  return response;
}

async function refreshOnce() {
  const response = await fetch(`${BASE}/api/v1/auth/refresh-token`, {
    method: "POST",
    credentials: "include",
  });
  if (!response.ok) return false;
  const { access_token } = await response.json();
  saveAccessToken(access_token);
  return true;
}
```

Не пытайтесь обновлять токен при 401 на `/api/v1/auth/**` — это приведёт к циклу.
Отдельно: `401` на `/api/v1/auth/authenticate` — это неверный email/пароль, а не
истёкшая сессия.

---

## Эндпоинты

### Auth

#### `POST /api/v1/auth/register`

Создаёт пользователя, сразу выдаёт `access_token` и устанавливает refresh-cookie.
Роль всегда `USER`; поле `role` в запросе не поддерживается (можно не отправлять).

**Auth:** не требуется.

| Поле | Тип | Обязательно | Ограничения |
|---|---|---|---|
| `nickname` | string | да | не пустой |
| `email` | string | да | валидный email, уникальный |
| `password` | string | да | минимум 8 символов |

```json
{
  "nickname": "demo",
  "email": "demo@example.com",
  "password": "password123"
}
```

**200 OK**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Плюс заголовок `Set-Cookie` с refresh-токеном (`HttpOnly; Secure; SameSite=Lax; Path=/api/v1`).
Поля `refresh_token` в теле нет.

**Ошибки:** `400` — невалидные данные или email уже занят; `409` — гонка при одновременной
регистрации того же email; `429` — rate limit.

> После регистрации запись баланса создаётся **асинхронно** через Kafka. Первый запрос баланса
> сразу после регистрации может вернуть `400` («Balance ... not found»). Повторите через
> 300–1000 мс, лучше — с backoff.

---

#### `POST /api/v1/auth/authenticate`

Логин по email и паролю.

**Auth:** не требуется.

```json
{
  "email": "demo@example.com",
  "password": "password123"
}
```

**200 OK** — только `access_token`, как у регистрации, плюс `Set-Cookie` с refresh-токеном.

**Ошибки:** `400` — невалидное тело; `401` — неверный email/пароль; `429` — rate limit.

---

#### `POST /api/v1/auth/refresh-token`

Обменивает refresh-токен из cookie на новый `access_token`.

**Auth:** не требуется заголовок. Refresh-токен читается из cookie `refresh_token`,
поэтому в браузере обязателен `credentials: 'include'`.

**Тело:** отсутствует.

**200 OK**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

Плюс `Set-Cookie` с **новым** refresh-токеном (ротация: старый инвалидируется).

**Ошибки:** `401` — cookie отсутствует, токен невалиден/истёк/уже ротирован/удалён.
Если cookie не пришла, тело ответа пустое.

---

#### `POST /api/v1/logout`

Инвалидирует текущую сессию: access token уходит в blacklist, refresh-токен удаляется
на сервере, cookie очищается.

**Auth:** `Authorization: Bearer <access_token>` **и** cookie `refresh_token`
(браузер отправляет её сам). Access token нужен gateway-у, чтобы определить пользователя
и передать `X-User-Name`.

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**200 OK** — пустое тело + `Set-Cookie: refresh_token=; Path=/api/v1; Max-Age=0; HttpOnly; Secure; SameSite=Lax`.

**Ошибки:** `400` — refresh-cookie не пришла; `401` — access token невалиден/истёк/уже в blacklist,
refresh-токен неизвестен или принадлежит другому пользователю.

> После logout access token нельзя использовать до истечения его срока (он в blacklist).
> Локальные токены нужно удалить в любом случае, даже если запрос logout не удался.
> Если access token уже истёк, сначала вызовите `refresh-token`, иначе logout вернёт `401`.

---

### Users

#### `GET /api/v1/users/{user_email}`

Публичный профиль пользователя.

**Auth:** не требуется.

**200 OK**

```json
{
  "nickname": "demo",
  "email": "demo@example.com",
  "role": "USER"
}
```

`role`: `USER` | `ADMIN`.

**Ошибки:** `401` — пользователь с таким email не найден (**именно 401, не 404**).

> Эндпоинта обновления профиля сейчас **нет**. Правка nickname на фронте хранится только
> локально и не сохраняется на сервере.

---

### Clicker

#### `PATCH /api/v1/clicker`

Увеличивает баланс текущего пользователя. Пользователь определяется по access token,
email в теле/URL не передаётся.

**Auth:** обязательна.

```json
{
  "balance_increase": 1
}
```

| Поле | Тип | Обязательно | Контракт |
|---|---|---|---|
| `balance_increase` | integer | да | 1–5; для клика отправляйте `1` |

**200 OK** — пустое тело.

**Ошибки:** `401` — нет/невалиден access token; `429` — rate limit.

> Текущая реализация не проверяет отрицательные и слишком большие значения. Клиент обязан
> ограничивать `balance_increase` диапазоном 1–5 (фактически — отправлять `1`).
> Если запись баланса ещё не создана, запрос вернёт `200`, но баланс не изменится.

---

#### `GET /api/v1/clicker/balance/{user_email}`

Возвращает баланс пользователя.

**Auth:** обязательна.

**200 OK**

```json
{
  "user_balance": 42
}
```

**Ошибки:** `400` — баланс для пользователя не найден (например, сразу после регистрации);
`401` — нет/невалиден access token; `429` — rate limit.

> Баланс другого пользователя тоже можно прочитать, если знать email (публичные данные).

---

### Leaderboard

#### `GET /api/v1/leaderboard`

Топ-10 игроков по балансу, по убыванию.

**Auth:** не требуется.

**200 OK**

```json
{
  "items": [
    {
      "balance": 120,
      "user_nickname": "demo",
      "user_email": "demo@example.com"
    },
    {
      "balance": 90,
      "user_nickname": "friend",
      "user_email": "friend@example.com"
    }
  ]
}
```

- Максимум 10 элементов.
- Порядок при равных балансах не гарантирован.
- Данные обновляются в реальном времени; кэша нет.

**Ошибки:** `429` — rate limit.

---

### Transfers

#### `POST /api/v1/transfers`

Создаёт перевод. Инициатор определяется по access token.

**Auth:** обязательна.

| Поле | Тип | Обязательно | Ограничения |
|---|---|---|---|
| `target_user_email` | string | да | валидный email, не совпадает с email инициатора |
| `transfer_amount` | integer | да | > 0, целое |
| `additional_message` | string | нет | максимум 255 символов |

```json
{
  "target_user_email": "friend@example.com",
  "transfer_amount": 10,
  "additional_message": "gg"
}
```

**202 Accepted**

```json
{
  "transfer_id": 17,
  "transfer_amount": 10,
  "initiator_email": "demo@example.com",
  "target_email": "friend@example.com",
  "additional_message": "gg",
  "status": "PENDING",
  "created_at": "2026-11-14T09:15:30.123456Z"
}
```

**Ошибки:**

- `400` — невалидное тело, перевод самому себе;
- `401` — нет/невалиден access token;
- `429` — rate limit.

> `202` означает «принято в обработку», а не «деньги уже переведены». Итоговый статус
> (`SUCCESS` / `FAILED`) появится позже в `GET /api/v1/transfers`.
>
> Наличие получателя и достаточность средств **не проверяются** в момент создания перевода.
> Если получателя нет или у инициатора не хватает денег, перевод станет `FAILED`.
>
> Идемпотентности нет: повторный `POST` создаст второй перевод. Блокируйте кнопку отправки
> на время запроса и не делайте автоматических retry.

---

#### `GET /api/v1/transfers`

История переводов текущего пользователя — и исходящих, и входящих.

**Auth:** обязательна.

**200 OK**

```json
{
  "transfers": [
    {
      "transfer_id": 18,
      "transfer_amount": 5,
      "initiator_email": "friend@example.com",
      "target_email": "demo@example.com",
      "additional_message": null,
      "status": "SUCCESS",
      "created_at": "2026-11-14T09:20:00.000000Z"
    },
    {
      "transfer_id": 17,
      "transfer_amount": 10,
      "initiator_email": "demo@example.com",
      "target_email": "friend@example.com",
      "additional_message": "gg",
      "status": "PENDING",
      "created_at": "2026-11-14T09:15:30.123456Z"
    }
  ]
}
```

- Сортировка: `created_at` по убыванию (сначала новые).
- Направление определяется на клиенте: `initiator_email === currentUser.email` → исходящий.
- Пагинации нет: возвращается вся история. Закладывайте виртуализацию списка.
- Отдельного `GET /api/v1/transfers/{id}` нет.

**Ошибки:** `401` — нет/невалиден access token; `429` — rate limit.

#### Статусы перевода

| Статус | Значение | Терминальный |
|---|---|---|
| `PENDING` | принят, saga выполняется | нет |
| `SUCCESS` | деньги списаны и зачислены | да |
| `FAILED` | перевод не выполнен (нет средств, нет получателя и т.п.) | да |

**Рекомендуемый polling:** после `POST` опрашивайте `GET /api/v1/transfers` раз в 1–2 секунды,
пока есть `PENDING`; остановитесь через ~30 секунд. Учитывайте лимит 5 запросов/сек на IP.

---

## Асинхронная модель

```
POST /transfers ──► transfer-service ──► Kafka ──► clicker-service
       │                                              │
       │ 202 PENDING                                  │ списание/зачисление
       ▼                                              ▼
GET /transfers  ◄──────── статус SUCCESS / FAILED ◄──┘
```

Что это значит для фронта:

1. После `POST /transfers` сразу показывайте операцию как «В обработке» (`PENDING`).
2. Не считайте баланс окончательным, пока перевод не стал `SUCCESS`/`FAILED`.
3. Обновляйте баланс и историю после терминального статуса.
4. После регистрации баланс тоже появляется не мгновенно (Kafka → clicker-service).

---

## Ошибки

### Единый формат для обработанных ошибок

```json
{
  "timestamp": "2026-11-14T12:34:56.789",
  "error": "Bad request.",
  "message": "User cannot transfer to himself."
}
```

- `timestamp` — локальное время сервера **без таймзоны**.
- `error` — короткая категория (`Bad request.`, `Unauthorized.`, `Not found.`, `Conflict.`).
- `message` — текст для разработчика; **не показывайте его пользователю как есть**,
  маппьте статусы на свои сообщения.

### Исключения из формата

| Ситуация | Тело ответа |
|---|---|
| Gateway отклонил access token (нет, истёк, неверный тип, blacklist) | **пустое** |
| Security-фильтр сервиса вернул 401/403 | `{"error":"Unauthorized","message":"..."}` |
| Rate limit (429) | обычно пустое, заголовки `X-RateLimit-*` |
| Ошибки валидации Spring (`400`) | стандартное тело Spring, может отличаться |
| 5xx от gateway | `{"timestamp":...,"error":"...","message":"..."}` |

**Практическое правило:** принимайте решения по HTTP-статусу, а `message` используйте только
для логов/тостов разработчика.

### Коды

| Код | Когда |
|---|---|
| `200` | успешный синхронный запрос |
| `202` | перевод принят в асинхронную обработку |
| `400` | валидация, перевод себе, баланс не найден, дубль email, отсутствие refresh-cookie в logout |
| `401` | нет/просрочен/невалиден токен, неверный логин, пользователь не найден |
| `403` | недостаточно прав (в обычном флоу не встречается) |
| `404` | неверный путь (нет такого route в gateway) |
| `409` | конфликт при регистрации (гонка по email) |
| `429` | превышен rate limit |
| `500` | внутренняя ошибка сервиса или gateway |
| `502` / `503` / `504` | сервис недоступен / gateway timeout (5 секунд) |

---

## Лимиты и CORS

### Rate limit (на IP, Redis)

| Группа | Путь | Replenish rate | Burst |
|---|---|---|---|
| Auth/Users/Logout | `/api/v1/auth/**`, `/api/v1/users/**`, `/api/v1/logout/**` | 5 req/s | 10 |
| Clicker/Leaderboard | `/api/v1/clicker/**`, `/api/v1/leaderboard` | 10 req/s | 20 |
| Transfers | `/api/v1/transfers/**` | 5 req/s | 10 |

При превышении — `429 Too Many Requests`. Заголовки лимитера (если присутствуют):
`X-RateLimit-Replenish-Rate`, `X-RateLimit-Burst-Capacity`, `X-RateLimit-Remaining`.

Рекомендации:

- кликер — не чаще 10 раз в секунду, лучше throttle ~100 мс;
- polling переводов — не чаще 1 раза в секунду;
- на `429` — экспоненциальный backoff, не мгновенный retry;
- лимит общий на IP, поэтому за одним NAT он делится между пользователями.

### CORS

Gateway разрешает следующие origin:

- `http://localhost:5500`
- `http://127.0.0.1:5500`
- `http://localhost:8085`
- `http://127.0.0.1:8085`
- `https://clicker.asphalt.by`

Список задаётся переменной `CORS_ALLOWED_ORIGINS` (через запятую).

Разрешены методы `GET, POST, PUT, DELETE, PATCH, OPTIONS`, любые заголовки,
`Access-Control-Allow-Credentials: true`. Preflight `OPTIONS` проходит без авторизации.

Следствия:

- в production фронт и API на одном origin (`https://clicker.asphalt.by`), CORS не участвует;
- при кросс-origin запросах (например, `localhost:5500` → `localhost:8080`) обязателен
  `credentials: 'include'`, иначе браузер не сохранит `Set-Cookie` и не отправит cookie;
- открывать фронт через `file://` нельзя — origin будет `null`, браузер заблокирует запросы;
- Vite на `5173` или webpack на `3000` по умолчанию **не пройдут CORS** — используйте
  `8085` (Docker-фронт) / `5500` или добавьте свой origin в `CORS_ALLOWED_ORIGINS`.

---

## Модели данных

### AuthTokens

| Поле | Тип | Описание |
|---|---|---|
| `access_token` | string | JWT, ~15 минут |

Refresh-токен в теле не передаётся. Он приходит в `Set-Cookie` (`refresh_token`,
~7 дней, одноразовый, ротируется) и хранится браузером как HttpOnly cookie.

### RefreshTokenCookie

| Свойство | Значение |
|---|---|
| Имя | `refresh_token` |
| `HttpOnly` | да — недоступна из JavaScript |
| `Secure` | да — только по HTTPS |
| `SameSite` | `Lax` |
| `Path` | `/api/v1` |
| `Max-Age` | `refreshTokenExpiration / 1000` секунд (по умолчанию 7 дней) |
| Установка | `register`, `authenticate`, `refresh-token` |
| Удаление | `logout` (`Max-Age=0`) |

### UserProfile

| Поле | Тип | Описание |
|---|---|---|
| `nickname` | string | отображаемое имя |
| `email` | string | уникальный email |
| `role` | `USER` \| `ADMIN` | роль |

### Balance

| Поле | Тип |
|---|---|
| `user_balance` | integer |

### LeaderboardItem

| Поле | Тип |
|---|---|
| `balance` | integer |
| `user_nickname` | string |
| `user_email` | string |

### Transfer

| Поле | Тип | Описание |
|---|---|---|
| `transfer_id` | integer | ID перевода |
| `transfer_amount` | integer | сумма в coin |
| `initiator_email` | string | отправитель |
| `target_email` | string | получатель |
| `additional_message` | string \| null | сообщение |
| `status` | `PENDING` \| `SUCCESS` \| `FAILED` | статус |
| `created_at` | string (ISO-8601 UTC) | время создания |

### ErrorResponse

| Поле | Тип |
|---|---|
| `timestamp` | string (local date-time) |
| `error` | string |
| `message` | string |

---

## Чек-лист интеграции

- [ ] Все запросы идут на gateway (`http://localhost:8080` локально,
  `https://clicker.asphalt.by` в проде), не на порты сервисов.
- [ ] `Content-Type: application/json` на `POST`/`PATCH` с телом.
- [ ] `credentials: 'include'` во всех `fetch`.
- [ ] Access token в `Authorization: Bearer ...` на защищённых эндпоинтах.
- [ ] `refresh_token` не читается из JSON и не хранится в `localStorage`/`sessionStorage`.
- [ ] Refresh-токен **не** отправляется в `Authorization` и не кладётся в тело logout.
- [ ] Refresh — single-flight, с сохранением нового `access_token` из ответа.
- [ ] На `401` — один refresh + один повтор, затем logout/логин.
- [ ] `401` на `/authenticate` показывается как «неверный email или пароль», без refresh.
- [ ] Перед logout при истёкшем access token выполняется refresh.
- [ ] Кнопка клика throttled (≤ 10 req/s), кнопка перевода блокируется на время запроса.
- [ ] После регистрации баланс запрашивается с retry/backoff.
- [ ] Перевод показывается как `PENDING`, затем polling до `SUCCESS`/`FAILED`.
- [ ] Баланс и история обновляются после терминального статуса перевода.
- [ ] `429` обрабатывается backoff-ом.
- [ ] Локальная разработка — через разрешённый origin (`8085` / `5500`, CORS).
- [ ] `X-User-Name` вручную не отправляется.
- [ ] Обновление профиля на сервере отсутствует — не показывайте ложное «сохранено».

---

## Известные ограничения бэкенда

1. **Нет обновления профиля** (`PATCH /users` отсутствует).
2. **Нет идемпотентности переводов** — повторный POST создаёт новую операцию.
3. **Нет пагинации** в истории переводов и отдельных `GET /transfers/{id}`.
4. **Нет проверки получателя при создании перевода** — ошибка выясняется асинхронно.
5. **Нет причины отказа перевода** — в ответе только `status: FAILED`, без кода/текста ошибки.
6. **Нет отмены перевода** после создания.
7. **Ограничение `balance_increase` не валидируется сервером** — клиент должен отправлять `1`.
8. **CORS ограничен списком origin в `CORS_ALLOWED_ORIGINS`** — см. раздел CORS.
9. **Нет WebSocket/SSE** — статусы только polling-ом.
10. **Публичные данные** — email и баланс видны в лидерборде, баланс читается по email.

Если какой-то из пунктов блокирует интеграцию, это нужно обсудить с бэкенд-командой
до начала разработки UI.
