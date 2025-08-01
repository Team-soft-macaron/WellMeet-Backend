# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

WellMeet Backend is a premium dining platform API that provides AI-powered restaurant recommendations and reservation services. It's built with Spring Boot 3.5.3, Java 21, and follows a multi-module architecture.

## Build Commands

### Building the Project
```bash
./gradlew clean build                # Build all modules
./gradlew clean build -x test        # Build without running tests
./gradlew :api-user:bootRun          # Run user API locally
./gradlew :api-owner:bootRun         # Run owner API locally
```

### Testing
```bash
./gradlew test                       # Run all tests
./gradlew :api-user:test            # Run user API tests only
./gradlew :api-owner:test           # Run owner API tests only
./gradlew test --tests "*ControllerTest"  # Run specific test pattern
```

### Database Setup
- Local development uses MySQL on port 3306
- Database name: `wellmeet`
- Username: `root` (no password for local)
- Schema initialization: automatic via `schema.sql` in domain module

## Architecture Overview

### Multi-Module Structure
```
├── common/          # Shared utilities, base classes, exceptions
├── domain/          # Core business logic, entities, repositories
├── api-user/        # User-facing REST API (port 8080)
└── api-owner/       # Restaurant owner API (separate deployment)
```

### Module Dependencies
- `api-user` and `api-owner` depend on both `domain` and `common`
- `domain` depends on `common`
- Each API module is a standalone Spring Boot application

### Key Architectural Patterns

**Domain-Driven Design**: 
- Rich domain entities in `domain/src/main/java/com/wellmeet/domain/`
- Business logic lives in entities, not services
- Repository pattern for data access

**Entity Structure**:
- All entities extend `BaseEntity` (provides id, createdAt, updatedAt)
- Complex relationships: Restaurant ↔ Menu, Restaurant ↔ Review, Member ↔ Reservation
- Status management via enums (e.g., `ReservationStatus`)

**API Design**:
- Separate DTOs for requests/responses
- Controller → Service → Repository layer
- Global exception handling with custom error codes

### Testing Infrastructure

**Base Test Classes**:
- `BaseControllerTest`: REST API integration tests with RestAssured
- `BaseRepositoryTest`: JPA repository tests
- `DataBaseCleaner`: Custom extension for test data cleanup

**Test Pattern**:
```java
// Controllers use RestAssured with random port
given()
    .contentType(ContentType.JSON)
    .body(request)
.when()
    .post("/api/endpoint")
.then()
    .statusCode(200);
```

### Key Business Domains

**Restaurant Management**:
- Core entity: `Restaurant` with embedded `Address`, `BusinessHours`
- Menu management with categories and items
- Distance calculations for location-based search

**Reservation System**:
- Complex workflow: PENDING → CONFIRMED → COMPLETED/CANCELLED
- Premium reservation support
- Special requests handling

**Review System**:
- Rating with contextual tags (situation-based)
- Image upload support
- Owner response capability

**Member Features**:
- Favorite restaurants
- Reservation history
- Preference tracking

### Configuration

**Spring Profiles**:
- `local`: Local development with MySQL
- `dev`: Development environment (uses dev-secret.yml)

**CORS Configuration**:
- Configured per environment in application-{profile}.yml
- Local: http://localhost:5173

### CI/CD Pipeline

**GitHub Actions**:
- PR to develop triggers `Dev_CI.yml` (build + test)
- Separate CD pipelines for user and owner APIs
- MySQL service container for CI tests

## Development Tips

### Running Locally
1. Ensure MySQL is running on port 3306
2. Create database: `CREATE DATABASE wellmeet;`
3. Run either api-user or api-owner module
4. API will initialize schema automatically

### Adding New Features
1. Start with domain entity in `domain` module
2. Create repository interface extending `JpaRepository`
3. Add business logic to entity methods
4. Create DTOs in API module
5. Implement controller with proper exception handling
6. Write integration tests extending `BaseControllerTest`

### Common Gotchas
- Each API module has its own main class and port configuration
- Test data is cleaned between tests via `DataBaseCleaner`
- Distance calculations use Haversine formula in `DistanceCalculator`
- All monetary values are stored as `BigDecimal`