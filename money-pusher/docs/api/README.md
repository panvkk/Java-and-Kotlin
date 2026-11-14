# Money Pusher — API v1

Документ для команды фронтенда. Описывает публичный HTTP API, доступный через `api-gateway`.

- **Base URL (локально):** `http://localhost:8080`
- **Формат:** JSON, `Content-Type: application/json`
- **Авторизация:** `Authorization: Bearer <access_token>`
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

# 1. Регистрация (сразу возвращает токены)
curl -s -X POST "$BASE/api/v1/auth/register" \
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
```

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

Все защищённые эндпоинты требуют **access token**. Gateway сам проверяет подпись, срок жизни,
тип токена (`ACCESS`) и blacklist, после чего прокидывает email пользователя во внутренний
заголовок `X-User-Name`. **Фронтенд не должен отправлять `X-User-Name` вручную.**

---

## Аутентификация

### Схема

1. `POST /api/v1/auth/register` или `POST /api/v1/auth/authenticate` → пара токенов.
2. `access_token` передаётся в `Authorization: Bearer ...` для защищённых запросов.
3. `access_token` живёт **15 минут** по умолчанию, `refresh_token` — **7 дней**.
4. Перед истечением access token обменяйте refresh token на новую пару через
   `POST /api/v1/auth/refresh-token`.
5. При logout отправьте оба токена: access попадёт в blacklist, refresh будет удалён.

### Важно про refresh

- Refresh-токен передаётся **в заголовке** `Authorization: Bearer <refresh_token>`, тела нет.
- Refresh-токен **не подходит** для защищённых эндпоинтов: gateway пропускает только токены
  с типом `ACCESS`.
- При обновлении refresh-токен **ротируется**: старый сразу становится недействительным.
  Поэтому два параллельных refresh (например, из двух вкладок) приведут к ошибке одного из них.
  Делайте single-flight: один refresh на всё приложение/вкладки.

### Рекомендуемая обработка 401

```js
// Псевдокод
async function authorizedFetch(url, options) {
  let response = await fetch(url, withAuth(options));

  if (response.status === 401) {
    const refreshed = await refreshOnce(); // single-flight
    if (!refreshed) {
      clearSession();
      location.replace("index.html");
      return response;
    }
    response = await fetch(url, withAuth(options)); // один повтор
  }

  return response;
}
```

Не пытайтесь обновлять токен при 401 на `/api/v1/auth/**` — это приведёт к циклу.

---

## Эндпоинты

### Auth

#### `POST /api/v1/auth/register`

Создаёт пользователя и сразу возвращает токены. Роль всегда `USER`; поле `role` в запросе
не поддерживается (можно не отправлять).

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
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

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

**200 OK** — та же пара токенов, что у регистрации.

**Ошибки:** `400` — невалидное тело; `401` — неверный email/пароль; `429` — rate limit.

---

#### `POST /api/v1/auth/refresh-token`

Обменивает refresh token на новую пару токенов.

**Auth:** `Authorization: Bearer <refresh_token>`

**Тело:** отсутствует.

**200 OK**

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Ошибки:** `401` — заголовок отсутствует, токен невалиден/истёк/уже ротирован/удалён.
При отсутствии заголовка ответ может быть с пустым телом.

---

#### `POST /api/v1/logout`

Инвалидирует текущую сессию.

**Auth:** `Authorization: Bearer <access_token>`

```json
{
  "access_token": "eyJhbGciOiJIUzI1NiJ9...",
  "refresh_token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**200 OK** — пустое тело.

**Ошибки:** `400` — не переданы токены; `401` — access token невалиден/истёк/уже в blacklist,
refresh token неизвестен или принадлежит другому пользователю.

> После logout access token нельзя использовать до истечения его срока (он в blacklist).
> Локальные токены нужно удалить в любом случае, даже если запрос logout не удался.

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
| `400` | валидация, перевод себе, баланс не найден, дубль email |
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

Сейчас gateway разрешает **только**:

- `http://localhost:5500`
- `http://127.0.0.1:5500`

Разрешены методы `GET, POST, PUT, DELETE, PATCH, OPTIONS`, любые заголовки,
`credentials: true`. Preflight `OPTIONS` проходит без авторизации.

Следствия:

- открывать фронт через `file://` нельзя — origin будет `null`, браузер заблокирует запросы;
- Vite на `5173` или webpack на `3000` по умолчанию **не пройдут CORS**;
- для локальной разработки используйте порт `5500` (например, Live Server) или попросите
  бэкенд добавить ваш origin в `CorsConfig`;
- для деплоя понадобится добавить боевой домен в `CorsConfig`.

---

## Модели данных

### AuthTokens

| Поле | Тип | Описание |
|---|---|---|
| `access_token` | string | JWT, ~15 минут |
| `refresh_token` | string | JWT, ~7 дней, одноразовый (ротируется) |

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

- [ ] Все запросы идут на `http://localhost:8080`, не на порты сервисов.
- [ ] `Content-Type: application/json` на `POST`/`PATCH`.
- [ ] Access token в `Authorization: Bearer ...` на защищённых эндпоинтах.
- [ ] Refresh token **не** используется для обычных запросов.
- [ ] Refresh — single-flight, с заменой обоих токенов.
- [ ] На `401` — один refresh + один повтор, затем logout.
- [ ] Кнопка клика throttled (≤ 10 req/s), кнопка перевода блокируется на время запроса.
- [ ] После регистрации баланс запрашивается с retry/backoff.
- [ ] Перевод показывается как `PENDING`, затем polling до `SUCCESS`/`FAILED`.
- [ ] Баланс и история обновляются после терминального статуса перевода.
- [ ] `429` обрабатывается backoff-ом.
- [ ] Локальная разработка — через `http://localhost:5500` (CORS).
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
8. **CORS ограничен двумя localhost-origin** — см. раздел CORS.
9. **Нет WebSocket/SSE** — статусы только polling-ом.
10. **Публичные данные** — email и баланс видны в лидерборде, баланс читается по email.

Если какой-то из пунктов блокирует интеграцию, это нужно обсудить с бэкенд-командой
до начала разработки UI.
