# Java and Kotlin

### В этом репозитории находятся 2 моих проекта на Spring:

## Money Pusher
подробнее в [/money-pusher](https://github.com/panvkk/Java-and-Kotlin/tree/main/money-pusher)

### Кратко:

 -- Цель проекта: Разработать полноценный микросервисный backend игрового сервиса с аутентификацией, балансом, лидербордом и переводами между пользователями. Делал акцент на безопасность и event-driven подход с правильной обработкой конкурентных операций.
 
 -- Стек: Java 17, Spring Boot, Spring Security, Spring Cloud Gateway, Spring Data JPA, Apache Kafka, PostgreSQL, Redis, Docker Compose, JWT, OpenAPI/Swagger.
 
 -- Деплой: сервис доступен по собственному домену - [clicker.asphalt.by](https://clicker.asphalt.by).
 
 -- Достижения: 
 
 - Спроектировал и реализовал 4 сервиса - auth, clicker, transfer и api-gateway - с отдельными PostgreSQL на сервис и общими модулями для JWT-утилит, event-контрактов и security-фильтров. 
 - Настроил JWT-аутентификацию с access/refresh-токенами, ротацией refresh-токенов, хранением в Redis и blacklist access-токенов при logout; на gateway реализовал глобальный фильтр проверки токена и проброс идентичности пользователя во внутренние сервисы через header. 
 - Реализовал переводы по паттерну Saga через Kafka: списание и зачисление средств, компенсация списания при неудачном зачислении. Для конкурентной безопасности баланса применил пессимистичные блокировки PostgreSQL и транзакции, для лидерборда - индекс по балансу и нативный top-N запрос. 

## Film Catalog
подробнее в [/film-catalog](https://github.com/panvkk/Java-and-Kotlin/tree/main/film-catalog)

### Кратко: 
 -- Цель проекта: Разработать учебный CRUD для управления каталогом фильмов и режиссёров с хранением данных в PostgreSQL. Использовал чистый Kotlin.
 
 -- Стек: Kotlin, Spring Boot, Spring Data JPA (Hibernate), PostgreSQL, Docker, OpenAPI (Swagger), JUnit 5, MockK, Gradle
 
 -- Достижения: Централизовал обработку ошибок, управлял транзакциями через @Transactional, подключил Swagger UI для документации API и покрыл бизнес-логику unit-тестами (JUnit 5 и MockK).
 
 -- Реализовал REST API с полным CRUD для фильмов и режиссёров: создание, частичное обновление, удаление и выборки с фильтрацией по названию, минимальному рейтингу, имени режиссёра и его фильмографии. Спроектировал слой хранения на JPA со связями ManyToOne/OneToMany.

## License
Distributed under the MIT License. See `LICENSE` for more information.
