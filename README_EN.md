# Explore With Me Plus

[🇷🇺 Русская версия](README.md)

---

## 📋 Project Description

**Explore With Me** is an event-sharing application that allows users to share information about interesting events and find companions to participate in them.

---

## 🚀 Implemented Stages

### ✅ Stage 1: Statistics Service (Stats Service)
A microservice for collecting and storing endpoint visit statistics.

**Features:**
- Saving request information to endpoints (`POST /hit`)
- Retrieving view statistics (`GET /stats`)
- Support for filtering by unique IP addresses
- Filtering by date range and URI list

**Technologies:**
- Spring Boot 3.5.0
- Spring Data JPA
- PostgreSQL
- MapStruct
- Lombok

### ✅ Stage 2: Statistics Service Client (Stats Client)
HTTP client for interacting with the statistics service from the main service.

**Features:**
- Sending event view information
- Retrieving statistics for displaying view counts

### ✅ Stage 3: DTO Assembly (Data Transfer Objects)
Common DTO classes for data exchange between services.

**Classes:**
- `EndpointHitDto` — endpoint visit data
- `ViewStatsDto` — view statistics

### ✅ Stage 4: Main Service - Events Module
Full-featured events management module — the core functionality of the application.

**Features:**
- **Public API:** View published events with filtering, sorting, and pagination
- **Private API:** Create, edit, and manage events by users
- **Admin API:** Event moderation (publish/reject), editing by administrators

**REST API:**
- `GET /events` — Public event search with filters (text, categories, paid, dates)
- `GET /events/{id}` — Get published event by ID
- `POST /users/{userId}/events` — Create event by user
- `GET /users/{userId}/events` — Get user's events
- `PATCH /users/{userId}/events/{eventId}` — Edit event by owner
- `GET /admin/events` — Admin event search
- `PATCH /admin/events/{eventId}` — Moderate event by admin

**Data Models:**
- `Event` — Main event entity (title, description, date, location, category, initiator)
- `Location` — Embeddable geolocation entity (latitude, longitude)
- `EventState` — State enumeration (PENDING, PUBLISHED, CANCELED)
- `User`, `Category` — Related entities

**Technologies:**
- Spring Boot 3.5.0
- Spring Data JPA (Specifications, Criteria API)
- PostgreSQL 16
- MapStruct
- Bean Validation
- 201 unit tests

---

## ✅ Stage 2: Splitting the Main Service into Microservices

### Extracted Services

- `event-service` — event, category, location and event moderation management.
- `request-service` — participation request management.
- `user-admin-service` — administrative user management.
- `extra-service` — additional functionality: comments, compilations, ratings and subscriptions.
- `main-domain` — shared domain module with DTOs, entities, repositories, mappers and business services moved out of `main-service`.
- `main-service` — transitional boot module; application startup and schema initialization remain here, while domain implementation has been moved out.

### Infrastructure

- `discovery-server` — Eureka service discovery.
- `config-server` — Spring Cloud Config Server in native mode, reading configurations from `classpath:/configurations`.
- `gateway-server` — single API entry point on port `8080`.
- `stats-service` — statistics service available through Gateway and directly on Docker port `9090`.

### Naming Rule

- business services use the `<domain>-service` pattern: `main-service`, `event-service`, `request-service`, `user-admin-service`, `extra-service`, `stats-service`;
- infrastructure services use the `<role>-server` pattern: `discovery-server`, `config-server`, `gateway-server`;
- the same service id is used by `spring.application.name`, Config Server file, Gateway route, Eureka and the Docker Compose service/container name.

### External API

All main API client requests go through Gateway: `http://localhost:8080`.

Public specifications:

- main service: `ewm-main-service-spec.json`;
- statistics service: `ewm-stats-service-spec.json`.

Main Gateway routes:

- `event-service`: `/events/**`, `/admin/events/**`, `/users/*/events/**`, `/categories/**`, `/admin/categories/**`, `/locations/**`, `/admin/locations/**`, `/internal/events/**`;
- `request-service`: `/users/*/requests/**`, `/users/*/events/*/requests/**`, `/admin/events/*/requests/**`, `/internal/requests/**`;
- `user-admin-service`: `/admin/users/**`;
- `extra-service`: `/events/*/comments/**`, `/users/*/comments/**`, `/admin/comments/**`, `/events/*/rating`, `/users/*/events/*/rating`, `/users/*/ratings`, `/users/*/subscriptions/**`, `/admin/compilations/**`, `/compilations/**`;
- `stats-service`: `/hit`, `/stats`.

### Internal Feign API

Internal contracts use Eureka service ids and OpenFeign:

- `event-service` -> `request-service`
  - `GET /internal/requests/events/{eventId}/count` — confirmed request count for an event.
- `request-service` -> `event-service`
  - `GET /internal/events/{eventId}/exists` — event existence check.

### Configuration

- Config Server: `infra/config-server/src/main/resources/application.yml`.
- Gateway routes: `infra/config-server/src/main/resources/configurations/gateway-server.yml`.
- Service configurations:
  - `infra/config-server/src/main/resources/configurations/event-service.yml`;
  - `infra/config-server/src/main/resources/configurations/request-service.yml`;
  - `infra/config-server/src/main/resources/configurations/user-admin-service.yml`;
  - `infra/config-server/src/main/resources/configurations/extra-service.yml`;
  - `infra/config-server/src/main/resources/configurations/main-service.yml`;
  - `infra/config-server/src/main/resources/configurations/stats-service.yml`.

### Verification

```bash
mvn install -P check
docker compose up --detach --build --force-recreate
npx newman run postman/ewm-main-service.json
npx newman run postman/ewm-stat-service.json
npx newman run postman/feature.json
```

---

## 🏗️ Project Architecture

```
explore-with-me/
├── core/
│   ├── main-domain/         # Shared domain module
│   ├── event-service/       # Event service
│   ├── request-service/     # Request service
│   ├── user-admin-service/  # User administration service
│   ├── extra-service/       # Additional functionality service
│   └── main-service/        # Transitional boot module
├── infra/
│   ├── gateway-server/      # API Gateway
│   ├── discovery-server/    # Eureka
│   └── config-server/       # Config Server
├── stats-service/           # Statistics service
├── docker-compose.yml
└── pom.xml                  # Parent POM
```

---

## 🛠️ Technology Stack

| Technology | Version |
|------------|---------|
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

## 🚀 Running the Project

### Using Docker Compose

```bash
docker-compose up -d
```

### Locally

```bash
# Build the project
mvn clean package

# Run the statistics service
cd stats-service
mvn spring-boot:run
```

---

## 📡 API Endpoints

### Stats Service (port 9090)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/hit` | Save request information |
| GET | `/stats` | Retrieve view statistics |

### Main Service - Events (port 8080)

#### Public API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/events` | Search events with filters |
| GET | `/events/{id}` | Get event by ID |

#### Private API (for authenticated users)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users/{userId}/events` | Create event |
| GET | `/users/{userId}/events` | User's events |
| GET | `/users/{userId}/events/{eventId}` | User's event by ID |
| PATCH | `/users/{userId}/events/{eventId}` | Edit event |

#### Admin API
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/events` | Search events (admin) |
| PATCH | `/admin/events/{eventId}` | Moderate event |

#### Example POST /hit Request

```json
{
  "app": "ewm-main-service",
  "uri": "/events/1",
  "ip": "192.168.1.1",
  "timestamp": "2024-01-15 10:30:00"
}
```

#### Example GET /stats Request

```
GET /stats?start=2024-01-01 00:00:00&end=2024-12-31 23:59:59&uris=/events/1&unique=true
```

---

## 👥 Authors

@DokPlay @Ibragim1111 @VanoStreyPracticum

---

## 📄 License

MIT
