# Explore With Me Plus

[🇬🇧 English version](README_EN.md)

---

## 📋 Описание проекта

**Explore With Me** — это приложение-афиша, которое позволяет пользователям делиться информацией об интересных событиях и находить компанию для участия в них.

---

## 🚀 Реализованные этапы

### ✅ Этап 1: Сервис статистики (Stats Service)
Микросервис для сбора и хранения статистики посещений эндпоинтов.

**Функциональность:**
- Сохранение информации о запросах к эндпоинтам (`POST /hit`)
- Получение статистики просмотров (`GET /stats`)
- Поддержка фильтрации по уникальным IP-адресам
- Фильтрация по диапазону дат и списку URI

**Технологии:**
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- MapStruct
- Lombok

### ✅ Этап 2: Клиент сервиса статистики (Stats Client)
HTTP-клиент для взаимодействия с сервисом статистики из основного сервиса.

**Функциональность:**
- Отправка информации о просмотрах событий
- Получение статистики для отображения количества просмотров

### ✅ Этап 3: Сборка DTO (Data Transfer Objects)
Общие DTO-классы для обмена данными между сервисами.

**Классы:**
- `EndpointHitDto` — данные о посещении эндпоинта
- `ViewStatsDto` — статистика просмотров

### ✅ Этап 4: Основной сервис - Модуль событий (Main Service - Events)
Полноценный модуль управления событиями — ключевая функциональность приложения.

**Функциональность:**
- **Публичный API:** Просмотр опубликованных событий с фильтрацией, сортировкой и пагинацией
- **Приватный API:** Создание, редактирование и управление событиями пользователями
- **Административный API:** Модерация событий (публикация/отклонение), редактирование администраторами

**REST API:**
- `GET /events` — Публичный поиск событий с фильтрами (текст, категории, платность, даты)
- `GET /events/{id}` — Получение опубликованного события по ID
- `POST /users/{userId}/events` — Создание события пользователем
- `GET /users/{userId}/events` — Получение событий пользователя
- `PATCH /users/{userId}/events/{eventId}` — Редактирование события владельцем
- `GET /admin/events` — Административный поиск событий
- `PATCH /admin/events/{eventId}` — Модерация события администратором

**Модели данных:**
- `Event` — Основная сущность события (название, описание, дата, локация, категория, инициатор)
- `Location` — Встраиваемая сущность геолокации (широта, долгота)
- `EventState` — Перечисление состояний (PENDING, PUBLISHED, CANCELED)
- `User`, `Category` — Связанные сущности

**Технологии:**
- Spring Boot 3.5.0
- Spring Data JPA (Specifications, Criteria API)
- PostgreSQL 16
- MapStruct
- Bean Validation
- 201 unit-тест

---

## ✅ Этап 2: Разбивка основного сервиса на микросервисы

### Выделенные сервисы

- `event-service` — управление мероприятиями, категориями, локациями и модерацией событий.
- `participation-service` — управление заявками на участие. Maven-модуль расположен в `core/request-service`.
- `user-admin-service` — административное управление пользователями.
- `extra-service` — дополнительная функциональность: комментарии, подборки, рейтинги, подписки.
- `main-domain` — общий доменный модуль с DTO, сущностями, репозиториями, мапперами и бизнес-сервисами, вынесенными из `main-service`.
- `main-service` — переходный boot-модуль; в нём оставлен запуск приложения и инициализация схемы, доменная реализация вынесена.

### Инфраструктура

- `discovery-server` — Eureka service discovery.
- `config-server` — Spring Cloud Config Server, работает в `native`-режиме и читает конфигурации из `classpath:/configurations`.
- `gateway-server` — единая входная точка API на порту `8080`.
- `stats-service` — сервис статистики, доступен через Gateway и напрямую на опубликованном Docker-порту `9090`.

### Внешний API

Все клиентские запросы к основному API проходят через Gateway: `http://localhost:8080`.

Публичные спецификации:

- основной сервис: `ewm-main-service-spec.json`;
- сервис статистики: `ewm-stats-service-spec.json`.

Основные маршруты Gateway:

- `event-service`: `/events/**`, `/admin/events/**`, `/users/*/events/**`, `/categories/**`, `/admin/categories/**`, `/locations/**`, `/admin/locations/**`, `/internal/events/**`;
- `participation-service`: `/users/*/requests/**`, `/users/*/events/*/requests/**`, `/admin/events/*/requests/**`, `/internal/requests/**`;
- `user-admin-service`: `/admin/users/**`;
- `extra-service`: `/events/*/comments/**`, `/users/*/comments/**`, `/admin/comments/**`, `/events/*/rating`, `/users/*/events/*/rating`, `/users/*/ratings`, `/users/*/subscriptions/**`, `/admin/compilations/**`, `/compilations/**`;
- `stats-service`: `/hit`, `/stats`.

### Внутренний API для Feign

Внутренние контракты используются сервисами через Eureka service-id и OpenFeign:

- `event-service` -> `participation-service`
  - `GET /internal/requests/events/{eventId}/count` — количество подтверждённых заявок события.
- `participation-service` -> `event-service`
  - `GET /internal/events/{eventId}/exists` — проверка существования события.

### Конфигурации

- Config Server: `infra/config-server/src/main/resources/application.yml`.
- Gateway routes: `infra/config-server/src/main/resources/configurations/gateway-server.yml`.
- Конфигурации сервисов:
  - `infra/config-server/src/main/resources/configurations/event-service.yml`;
  - `infra/config-server/src/main/resources/configurations/participation-service.yml`;
  - `infra/config-server/src/main/resources/configurations/user-admin-service.yml`;
  - `infra/config-server/src/main/resources/configurations/extra-service.yml`;
  - `infra/config-server/src/main/resources/configurations/main-service.yml`;
  - `infra/config-server/src/main/resources/configurations/stats-service.yml`.

### Проверка

```bash
mvn install -P check
docker compose up --detach --build --force-recreate
npx newman run postman/ewm-main-service.json
npx newman run postman/ewm-stat-service.json
npx newman run postman/feature.json
```

---

## 🏗️ Архитектура проекта

```
explore-with-me/
├── core/
│   ├── main-domain/         # Общий доменный модуль
│   ├── event-service/       # Сервис мероприятий
│   ├── request-service/     # Сервис заявок, регистрируется как participation-service
│   ├── user-admin-service/  # Сервис администрирования пользователей
│   ├── extra-service/       # Сервис дополнительного функционала
│   └── main-service/        # Переходный boot-модуль
├── infra/
│   ├── gateway-server/      # API Gateway
│   ├── discovery-server/    # Eureka
│   └── config-server/       # Config Server
├── stats-service/           # Сервис статистики
├── docker-compose.yml
└── pom.xml                  # Родительский POM
```

---

## 🛠️ Технологический стек

| Технология | Версия |
|------------|--------|
| Java | 21 LTS |
| Spring Boot | 3.5.0 |
| PostgreSQL | 16 |
| Maven | 3.9+ |
| Docker | 24+ |
| MapStruct | 1.5.5 |
| Lombok | 1.18.32 |
| JUnit 5 | 5.11.4 |
| Mockito | 5.x |

---

## 🚀 Запуск проекта

### С помощью Docker Compose

```bash
docker-compose up -d
```

### Локально

```bash
# Сборка проекта
mvn clean package

# Запуск сервиса статистики
cd stats-service
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Stats Service (порт 9090)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/hit` | Сохранение информации о запросе |
| GET | `/stats` | Получение статистики просмотров |

### Main Service - Events (порт 8080)

#### Публичный API
| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/events` | Поиск событий с фильтрами |
| GET | `/events/{id}` | Получение события по ID |

#### Приватный API (для авторизованных пользователей)
| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/users/{userId}/events` | Создание события |
| GET | `/users/{userId}/events` | События пользователя |
| GET | `/users/{userId}/events/{eventId}` | Событие пользователя по ID |
| PATCH | `/users/{userId}/events/{eventId}` | Редактирование события |

#### Административный API
| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/admin/events` | Поиск событий (админ) |
| PATCH | `/admin/events/{eventId}` | Модерация события |

#### Пример запроса POST /hit

```json
{
  "app": "ewm-main-service",
  "uri": "/events/1",
  "ip": "192.168.1.1",
  "timestamp": "2024-01-15 10:30:00"
}
```

#### Пример запроса GET /stats

```
GET /stats?start=2024-01-01 00:00:00&end=2024-12-31 23:59:59&uris=/events/1&unique=true
```

---

## 👥 Авторы

Команда 
@DokPlay 
@Ibragim1111
@VanoStreyPracticum

---

## 📄 Лицензия

MIT
