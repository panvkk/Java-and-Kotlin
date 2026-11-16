# Film Catalog

Учебный CRUD-сервис для управления каталогом фильмов и режиссёров. Проект
реализован на чистом Kotlin с хранением данных в PostgreSQL.

## Цель

Разработать REST API для:

- создания, частичного обновления и удаления фильмов и режиссёров;
- получения фильмов с фильтрацией по названию и минимальному рейтингу;
- поиска режиссёров по имени;
- получения фильмографии режиссёра.

## Стек

- Kotlin;
- Spring Boot;
- Spring Data JPA / Hibernate;
- PostgreSQL;
- Docker Compose;
- OpenAPI / Swagger UI;
- Gradle;
- JUnit 5 и MockK.

## Реализация

- централизованная обработка ошибок;
- управление транзакциями через `@Transactional`;
- JPA-связи `ManyToOne` и `OneToMany` между фильмами и режиссёрами;
- unit-тесты бизнес-логики на JUnit 5 и MockK;
- валидация входных данных на сервисном уровне.

## API

Основные endpoint-ы:

| Метод | Endpoint | Назначение |
| --- | --- | --- |
| `GET` | `/api/movies` | Список фильмов с фильтрацией |
| `POST` | `/api/movies` | Создание фильма |
| `PATCH` | `/api/movies/{movie_id}` | Частичное обновление фильма |
| `DELETE` | `/api/movies/{movie_id}` | Удаление фильма |
| `GET` | `/api/movies/{director_id}` | Фильмография режиссёра |
| `GET` | `/api/directors` | Список режиссёров с поиском по имени |
| `POST` | `/api/directors` | Создание режиссёра |
| `PATCH` | `/api/directors/{director_id}` | Обновление режиссёра |
| `DELETE` | `/api/directors/{director_id}` | Удаление режиссёра |

После запуска документация доступна в Swagger UI:

<http://localhost:8080/swagger-ui/index.html>

## Запуск

Требуются JDK 17+ и Docker.

Запустить PostgreSQL:

```bash
docker compose up -d
```

Запустить приложение:

```bash
.\gradlew.bat bootRun
```

Сервис использует PostgreSQL на `localhost:5432`:

- база: `cinema_db`;
- пользователь: `postgres`;
- пароль: `postgres_password`.

Собрать проект и запустить тесты:

```bash
.\gradlew.bat build
.\gradlew.bat test
```
