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
- 106 unit tests

---

## 🏗️ Project Architecture

```
explore-with-me/
├── main-service/           # Main Service
│   ├── event/              # Events module
│   │   ├── controller/     # Public/Private/Admin controllers
│   │   ├── service/        # Event business logic
│   │   ├── repository/     # JPA repositories
│   │   ├── dto/            # Event DTOs
│   │   ├── model/          # JPA entities (Event, Location)
│   │   └── mapper/         # MapStruct mappers
│   ├── user/               # Users module
│   ├── category/           # Categories module
│   ├── config/             # Configuration (StatsClient)
│   └── exception/          # Global error handling
├── stats-service/          # Statistics Service
│   ├── stats-dto/          # Common DTOs
│   ├── stats-client/       # HTTP client
│   └── stats-server/       # Stats REST API
└── pom.xml                 # Parent POM
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

Yandex Practicum Team

---

## 📄 License

This project was created as part of the Yandex Practicum training program.
