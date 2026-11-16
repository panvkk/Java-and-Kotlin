# Money Pusher

Money Pusher -- полноценный микросервисный backend игрового сервиса в формате кликера: пользователь увеличивает баланс, попадает в таблицу лидеров и может переводить coins другим пользователям. Проект сделан как практическая backend-задача на Java с разделением ответственности между сервисами, асинхронным обменом событиями и единой точкой входа для публичного API.

Проект доступен в публичном доступе по адресу
[clicker.asphalt.by](http://clicker.asphalt.by).

## Задача проекта

Нужно было реализовать сервис, в котором:

- пользователь регистрируется и входит в систему;
- баланс создаётся после регистрации;
- пользователь может получать внутреигровую валюту за клики;
- доступна таблица лидеров по балансу;
- пользователь может отправлять coins другому пользователю;
- перевод проходит через асинхронный workflow и получает итоговый статус;
- доступ к API защищён JWT, а refresh-токены передаются через HttpOnly cookie (с шифрованием);
- frontend предоставляет UI для основных пользовательских сценариев.

Главный акцент на backend: доменная логика, безопасность, согласованность балансов, конкурентные операции, event-driven взаимодействие.
Я выбирал именно такую предметную область, в основном чтобы проработать правильное использование Kafka в операции перевода средств, которая затрагивает несколько микросервисов и требует целую SAGA, идемпотентных апдейтов, outbox'а и тд.

## Стек

### Backend

- Java 17;
- Spring Boot 4.1.2;
- Spring Cloud Gateway и Spring WebFlux для API Gateway;
- Spring MVC для бизнес-сервисов;
- Spring Security;
- Spring Data JPA / Hibernate;
- PostgreSQL 16;
- Apache Kafka для событий между сервисами;
- Redis 8.4 для refresh-токенов, blacklist access-токенов и rate limiting;
- JJWT 0.11.5 для работы с JWT;
- Springdoc OpenAPI 3.1.0;
- Spring Boot Actuator;
- Lombok;
- Gradle 8.14 с Kotlin DSL.

### Frontend

Frontend сделан при помощи AI-агента **ChatGPT 5.6 Luna** с использованием
скиллов **hallmark** - для создания дизайна без ии-слопа и **modern-web-guidance** - для использования в коде фронта современного стэка методов.

Технически frontend использует React 18, TypeScript, Vite 6,
React Router 7, Tailwind CSS 3 и nginx для раздачи production-сборки.

### Инфраструктура

- Docker и Docker Compose;
- отдельный PostgreSQL-инстанс для каждого сервиса - auth, clicker и transfer;
- Kafka в KRaft-режиме;
- Kafka UI;
- nginx;
- healthcheck-и для инфраструктурных и application-контейнеров.

## Архитектура

```text
Frontend
    |
    v
API Gateway :8080
    |----------------------|----------------------|
    v                      v                      v
Auth Service :8081   Clicker Service :8082   Transfer Service :8083
    |                      |                      |
PostgreSQL              PostgreSQL              PostgreSQL
    \__________________________|__________________________/
                               v
                              Kafka

Redis используется в gateway и auth-service
```

### Модули

| Модуль | Назначение |
| --- | --- |
| `api-gateway` | Единая публичная точка входа, маршрутизация, проверка JWT (дальше в сервисы прокидает HEADER с userEmail), CORS и rate limiting |
| `auth-service` | Регистрация, аутентификация, refresh-token ротация , logout, профили пользователей |
| `clicker-service` | Балансы, начисление coins, списание/возврат средств, leaderboard |
| `transfer-service` | Создание и обработка переводов, история операций, outbox relay |
| `common-core` | Общие DTO событий, JWT/HMAC utilities, исключения и константы |
| `common-servlet` | Общая servlet-безопасность: одинаковый OncePerRequestFilter для каждого сервиса кроме api-gateway и security handlers |
| `frontend` | Клиентское приложение React/Vite |

Каждый бизнес-сервис владеет собственной PostgreSQL-базой. Сервисы не
обмениваются прямыми запросами к базам друг друга: для событий используется
Kafka.

## Реализованные достижения

- Спроектированы и реализованы четыре приложения: `auth-service`,
  `clicker-service`, `transfer-service` и `api-gateway`.
- На gateway реализован глобальный JWT-фильтр: он проверяет access token и
  передаёт идентичность пользователя во внутренние сервисы через header
  `X-User-Name`.
- Refresh-токены ротируются и хранятся в Redis; access-токены при logout
  добавляются в Redis blacklist до окончания срока действия.
- Переводы реализованы по паттерну Saga через Kafka: списание средств,
  зачисление получателю и компенсация списания при неудачном зачислении.
- Для конкурентной безопасности баланса используются транзакции и
  пессимистичные блокировки PostgreSQL.
- Для leaderboard добавлен индекс по `balance` и используется нативный
  top-N-запрос.

## Ключевые backend-сценарии

### Аутентификация

1. `POST /api/v1/auth/register` или
   `POST /api/v1/auth/authenticate` возвращает access token.
2. Refresh token отправляется через `Set-Cookie` как HttpOnly cookie
   `refresh_token`.
3. `POST /api/v1/auth/refresh-token` ротирует refresh token и возвращает новый
   access token.
4. `POST /api/v1/logout` добавляет access token в blacklist Redis, удаляет
   refresh token и очищает cookie.

Access token рассчитан примерно на 15 минут, refresh token -- примерно на
7 дней. Защищённые запросы используют
`Authorization: Bearer <access_token>`.

### Баланс и клики

После регистрации `auth-service` публикует событие создания пользователя.
`clicker-service` асинхронно создаёт баланс. Поэтому сразу после регистрации
баланс может быть ещё недоступен — клиенту следует повторить запрос.

### Переводы: Saga и конкурентная безопасность

`POST /api/v1/transfers` создаёт операцию и возвращает `202 Accepted` со
статусом `PENDING`. Дальше `transfer-service` и `clicker-service` обмениваются
Kafka-событиями:

1. создаётся заявка на перевод;
2. у отправителя списываются coins под транзакционной пессимистичной
   блокировкой;
3. средства зачисляются получателю;
4. при неудачном зачислении запускается компенсация списания;
5. операция получает статус `SUCCESS` или `FAILED`.

Состояния Saga (`DEDUCT`, `DEPOSIT`, `COMPENSATE`, `FINISH`) защищают обработку
от повторного применения одного и того же этапа. Для надёжной публикации
событий `transfer-service` использует outbox relay.
Идемпотентность `POST /transfers` не предусмотрена: повторный запрос создаёт
новую операцию.

## API

Все публичные запросы выполняются через gateway:
`http://localhost:8080`.

Основные группы endpoint-ов:

| Группа | Примеры |
| --- | --- |
| Auth | `/api/v1/auth/register`, `/api/v1/auth/authenticate`, `/api/v1/auth/refresh-token`, `/api/v1/logout` |
| Users | `/api/v1/users/{user_email}` |
| Clicker | `/api/v1/clicker/balance/{user_email}`, endpoint начисления баланса |
| Leaderboard | `/api/v1/leaderboard` |
| Transfers | `GET /api/v1/transfers`, `POST /api/v1/transfers` |

Полная спецификация доступна в
[`docs/api/openapi.yaml`](docs/api/openapi.yaml). Также спецификации
OpenAPI присутствуют в
[`frontend/api/openapi.yaml`](frontend/api/openapi.yaml).

Ограничения gateway по IP:

- auth и transfers: 5 запросов в секунду, burst 10;
- clicker и leaderboard: 10 запросов в секунду, burst 20;
- при превышении возвращается `429 Too Many Requests`.

Суммы передаются как целые `int64`, единица измерения — coin.

## Быстрый запуск через Docker Compose

Требования:

- Docker с поддержкой Compose;
- свободные порты `5432`, `5433`, `5434`, `6379`, `8080`, `8085`,
  `9092`, `8090`;
- для локального запуска backend без Docker — JDK 17+.

Из корня проекта:

```bash
cp .env.example .env
```

В `.env` обязательно замените `POSTGRES_PASSWORD` и `JWT_SECRET`. После этого:

```bash
docker compose up -d --build
```

Приложение будет доступно по адресам:

- frontend: <http://localhost:8085>;
- API gateway: <http://localhost:8080>;
- Kafka UI: <http://localhost:8090>;
- health gateway: <http://localhost:8080/actuator/health>.

Остановить окружение:

```bash
docker compose down
```

Остановить окружение вместе с данными PostgreSQL, Redis и Kafka:

```bash
docker compose down -v
```

Все опубликованные Docker-порты привязаны к `127.0.0.1`. Для production
предполагается, что внешний nginx завершает TLS и проксирует запросы к
локальным портам gateway и frontend.

## Запуск backend отдельно

Сначала поднимите инфраструктуру:

```bash
docker compose up -d postgres-auth postgres-clicker postgres-transfer redis kafka
```

Затем запустите приложения из корня проекта в отдельных терминалах:

```bash
.\gradlew.bat :auth-service:bootRun
.\gradlew.bat :clicker-service:bootRun
.\gradlew.bat :transfer-service:bootRun
.\gradlew.bat :api-gateway:bootRun
```

Конфигурация локального запуска читается из `.env`. Шаблон переменных окружения:
[`.env.example`](.env.example). Перед использованием скопируйте его в `.env`.

## Запуск frontend отдельно

```bash
cd frontend
npm ci
npm run dev
```

Для production-сборки:

```bash
npm run build
npm run preview
```

URL backend задаётся переменной `VITE_API_BASE_URL`. Это публичная build-time
переменная, она не должна содержать секреты.

## Сборка и тесты

Собрать все Java-модули:

```bash
.\gradlew.bat build
```

Запустить тесты:

```bash
.\gradlew.bat test
```

Собрать только конкретный сервис:

```bash
.\gradlew.bat :transfer-service:build
```

## Конфигурация и безопасность

- секреты и пароли не хранятся в репозитории; используйте `.env`;
- `JWT_SECRET` должен быть одинаковым для `auth-service` и `api-gateway`;
- refresh token недоступен JavaScript и не должен сохраняться в
  `localStorage` или `sessionStorage`;
- CORS задаётся через `CORS_ALLOWED_ORIGINS`;
- внутренние сервисы не публикуют свои порты наружу в Compose;
- в production TLS должен завершаться на внешнем nginx;
- `ddl-auto: update` оставлен для учебного/проектного окружения; перед
  production-развёртыванием миграции схемы следует перевести на
  специализированный инструмент, например Flyway или Liquibase.

## Структура репозитория

```text
money-pusher/
├── api-gateway/
├── auth-service/
├── clicker-service/
├── transfer-service/
├── common-core/
├── common-servlet/
├── frontend/
├── docs/api/openapi.yaml
├── docker-compose.yml
├── .env.example
├── build.gradle.kts
└── settings.gradle.kts
```

## License
Code is MIT licensed, all visual assets and lore are All Rights Reserved
