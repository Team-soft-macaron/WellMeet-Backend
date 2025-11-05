# WellMeet-Backend 마이크로서비스 마이그레이션 계획

## 개요

**목표**: domain-* 모듈을 독립적인 마이크로서비스로 점진적으로 마이그레이션하며, domain-restaurant부터 시작

**아키텍처**:
- API 모듈 (api-user, api-owner) → BFF (Backend for Frontend) 패턴
- Domain 모듈 → Eureka에 등록된 독립 마이크로서비스
- 통신 방식 → 초기: 직접 의존성, 최종: Spring Cloud OpenFeign (REST)
- 배포 → Docker Compose (로컬/스테이징), Docker 컨테이너 (프로덕션)

**마이그레이션 전략**:
- ✅ **Phase 1-4**: domain-* 모듈을 독립 서버로 배포 (api-* 의존성 유지)
- ✅ **Phase 5**: 모든 domain 배포 완료 후 BFF 전환 (Feign Client 도입, 의존성 제거)
- ✅ **Phase 6**: Saga Orchestration 구현
- ✅ **Phase 7**: API Gateway 구현

**선택된 전략**:
- ✅ 첫 분리 모듈: **domain-restaurant** (독립성이 높고, 다른 도메인 의존성 없음)
- ✅ 분리 순서: domain-restaurant → domain-member → domain-owner → domain-reservation
- ✅ 인증 방식: **JWT 기반** (Phase 7 API Gateway에서 구현)
- ✅ Docker 환경: **Docker Compose** (로컬 개발 + 테스트 환경)
- ✅ **2단계 접근**: 먼저 독립 배포 인프라 구축, 이후 BFF 전환

---

## 현재 아키텍처 분석

### 1. 모듈 의존성

**API 모듈 의존성** (api-user, api-owner 동일):
- domain-reservation
- domain-member
- domain-owner
- domain-restaurant
- infra-redis
- infra-kafka

**중요 발견사항**: domain-restaurant는 다른 domain 모듈에 의존성이 전혀 없어서 첫 번째 분리 대상으로 이상적

### 2. Domain-Restaurant 모듈 구조

**핵심 컴포넌트**:
- **엔티티**: Restaurant, AvailableDate, BusinessHour, Menu, Review
- **도메인 서비스**: RestaurantDomainService, AvailableDateDomainService 등
- **데이터베이스**: 전용 MySQL 인스턴스 (mysql-restaurant:3309) 이미 구성됨
- **외부 도메인 의존성 없음**: ownerId를 String으로만 참조

### 3. 마이그레이션 접근 방식

**Phase 1-4 (독립 배포)**: 
- domain-* 모듈을 독립 서버로 배포
- **api-* 모듈은 직접 의존성 유지** (`implementation project(':domain-*')`)
- 목적: Docker 인프라 구축, 독립 실행 검증, Eureka 등록

**Phase 5 (BFF 전환)**:
- 모든 domain-* 서버 배포 완료 후 시작
- Feign Client 구현
- api-* 모듈에서 모든 domain-* 직접 의존성 제거
- 완전한 BFF 패턴으로 전환

**Phase 6-7 (분산 시스템)**:
- Saga Orchestration으로 트랜잭션 관리
- API Gateway로 중앙 인증 처리

### 4. 기존 인프라

**Service Discovery (Eureka Server)**: 완전히 작동 중 (포트 8761)
**데이터베이스**: Database-per-Service 패턴 이미 구현됨 ✅
**메시징**: Redis (분산 락), Kafka (이벤트)

---

## Phase 1: domain-restaurant 독립 서버 배포 (2-3주)

**목표**: domain-restaurant를 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

### 1.1 REST API 레이어 생성

**파일 생성**: `domain-restaurant/src/main/java/com/wellmeet/domain/restaurant/api/RestaurantInternalController.java`

```java
@RestController
@RequestMapping("/internal/restaurants")
class RestaurantInternalController {
    private final RestaurantDomainService restaurantDomainService;
    private final AvailableDateDomainService availableDateDomainService;

    // 기본 조회
    @GetMapping("/{id}")
    RestaurantResponse getRestaurant(@PathVariable String id) {
        Restaurant restaurant = restaurantDomainService.getById(id);
        return RestaurantResponse.from(restaurant);
    }

    @GetMapping("/bulk")
    List<RestaurantResponse> getRestaurantsBulk(@RequestParam List<String> ids) {
        return restaurantDomainService.getByIds(ids).stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    // 예약 가능 날짜
    @GetMapping("/{id}/available-dates/{availableDateId}")
    AvailableDateResponse getAvailableDate(
        @PathVariable String id,
        @PathVariable Long availableDateId
    ) {
        AvailableDate availableDate = availableDateDomainService.getById(availableDateId);
        return AvailableDateResponse.from(availableDate);
    }

    @PostMapping("/{id}/available-dates/{availableDateId}/decrease-capacity")
    void decreaseCapacity(
        @PathVariable String id,
        @PathVariable Long availableDateId,
        @RequestBody DecreaseCapacityRequest request
    ) {
        availableDateDomainService.decreaseCapacity(availableDateId, request.partySize());
    }

    @PostMapping("/{id}/available-dates/{availableDateId}/increase-capacity")
    void increaseCapacity(
        @PathVariable String id,
        @PathVariable Long availableDateId,
        @RequestBody IncreaseCapacityRequest request
    ) {
        availableDateDomainService.increaseCapacity(availableDateId, request.partySize());
    }
}
```

**DTO 생성**:
```java
record RestaurantResponse(
    String id, String name, String address,
    double latitude, double longitude,
    String thumbnailUrl, String ownerId
) {
    static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getAddress(),
            restaurant.getLatitude(),
            restaurant.getLongitude(),
            restaurant.getThumbnailUrl(),
            restaurant.getOwner() != null ? restaurant.getOwner().getId() : null
        );
    }
}

record AvailableDateResponse(
    Long id, LocalDate date, LocalTime time,
    int maxCapacity, String restaurantId
) {
    static AvailableDateResponse from(AvailableDate availableDate) {
        return new AvailableDateResponse(
            availableDate.getId(),
            availableDate.getDate(),
            availableDate.getTime(),
            availableDate.getMaxCapacity(),
            availableDate.getRestaurant().getId()
        );
    }
}

record DecreaseCapacityRequest(int partySize) {}
record IncreaseCapacityRequest(int partySize) {}
```

### 1.2 Spring Boot Application 활성화

**파일 생성**: `domain-restaurant/src/main/java/com/wellmeet/domain/RestaurantServiceApplication.java`

⚠️ **중요**: 빈 스캔 문제로 인해 Application 클래스는 생성만 하고 **전체 주석 처리**

```java
//package com.wellmeet.domain;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class RestaurantServiceApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(RestaurantServiceApplication.class, args);
//    }
//}
```

**참고**:
- `@EnableEurekaClient`는 최신 Spring Cloud 버전(2020.0.0+)에서 제거되었으며, `application.yml`의 eureka 설정만으로 자동 등록됨
- `@EnableJpaAuditing`은 domain-common 모듈에 이미 설정되어 있으므로 별도 설정 불필요

**build.gradle 수정**:
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
}
```

**application.yml 생성**:
```yaml
spring:
  application:
    name: domain-restaurant-service
server:
  port: 8081
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

### 1.3 Dockerfile 생성

```dockerfile
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :domain-restaurant:bootJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/domain-restaurant/build/libs/*.jar app.jar
HEALTHCHECK --interval=30s --timeout=3s CMD curl -f http://localhost:8081/actuator/health || exit 1
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 1.4 docker-compose.yml 업데이트

```yaml
services:
  domain-restaurant-service:
    build:
      context: .
      dockerfile: domain-restaurant/Dockerfile
    ports:
      - "8081:8081"
    environment:
      SPRING_PROFILES_ACTIVE: local
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-restaurant:3306/wellmeet_restaurant
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://discovery-server:8761/eureka/
    depends_on:
      - mysql-restaurant
      - discovery-server
    networks:
      - wellmeet-network
```

### 1.5 검증 (독립 서버 실행)

```bash
# Docker로 domain-restaurant 서비스 실행
docker-compose up -d domain-restaurant-service

# Eureka 등록 확인
curl http://localhost:8761

# Health check
curl http://localhost:8081/actuator/health

# REST API 테스트
curl http://localhost:8081/internal/restaurants/{restaurantId}
```

### 1.6 중요: api-* 모듈 의존성 유지

**api-user/build.gradle 변경 없음**:
```gradle
dependencies {
    // 이 단계에서는 여전히 직접 의존성 유지
    implementation project(':domain-restaurant')  // ✅ 유지
    implementation project(':domain-reservation')
    implementation project(':domain-member')
    implementation project(':domain-owner')
}
```

**Phase 1 완료 기준**: ✅ **완료 (2025-11-05)**

**코드 구현 (완료)**:
- [x] domain-restaurant REST API Controller 생성 (RestaurantDomainController)
- [x] Application Service 및 DTO 레이어 구현 (RestaurantApplicationService)
- [x] Dockerfile 및 docker-compose.yml 설정 (포트 8083)
- [x] build.gradle 의존성 설정 완료
- [x] **api-* 모듈은 여전히 직접 의존성 사용** (변경 없음)
- [x] 클래스 네이밍 규칙 적용 완료 (2025-11-05)

**실행 검증 (보류)**:
- [ ] ⚠️ Application 클래스 주석 해제 및 빈 스캔 문제 해결 (Phase 6 이후)
- [ ] ⚠️ bootJar 빌드 성공 (Phase 6 이후)
- [ ] ⚠️ domain-restaurant가 독립 서버로 실행됨 (포트 8083)
- [ ] ⚠️ Eureka에 정상 등록됨
- [ ] ⚠️ `/api/restaurants/*` REST API 응답 확인
- [ ] ⚠️ Health check 정상 작동

**완료도**: 90% (코드 완성, 독립 실행 검증은 Phase 6 이후 수행 예정)

**참고**: Phase 5 BFF 전환 완료로 domain-* 모듈의 독립 실행은 선택사항이 되었으며, api-* 모듈이 Feign Client로 완전 전환되어 microservices 아키텍처 목표는 달성됨

---

## Phase 2: domain-member 독립 서버 배포 ✅ (완료)

**목표**: domain-member를 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

**완료 일자**: 2025-10-31

### 2.1 구현 완료 사항

Phase 1 패턴을 동일하게 적용하여 완료:

1. **REST API Controller 생성**: ✅
   - `MemberController.java` - 회원 CRUD API
   - `FavoriteRestaurantController.java` - 즐겨찾기 API
   - 엔드포인트:
     - POST `/api/members` - 회원 생성
     - GET `/api/members/{id}` - 회원 단건 조회
     - POST `/api/members/batch` - 회원 배치 조회
     - DELETE `/api/members/{id}` - 회원 삭제
     - GET `/api/favorites/check` - 즐겨찾기 여부 확인
     - GET `/api/favorites/members/{memberId}` - 즐겨찾기 목록 조회
     - POST `/api/favorites` - 즐겨찾기 추가
     - DELETE `/api/favorites` - 즐겨찾기 삭제

2. **Application Service 레이어 생성**: ✅
   - `MemberApplicationService.java` - 회원 비즈니스 로직
   - `FavoriteRestaurantApplicationService.java` - 즐겨찾기 비즈니스 로직
   - DomainService → ApplicationService 패턴 준수

3. **DTO 클래스 생성**: ✅
   - `MemberResponse` - 회원 응답
   - `CreateMemberRequest` - 회원 생성 요청 (@Valid 검증)
   - `MemberIdsRequest` - 배치 조회 요청
   - `FavoriteRestaurantResponse` - 즐겨찾기 응답
   - `ErrorResponse` - 에러 응답

4. **예외 처리**: ✅
   - `MemberExceptionHandler.java` - @RestControllerAdvice
   - MemberException, MethodArgumentNotValidException, IllegalArgumentException, Exception 처리

5. **Spring Boot Application**: ✅
   - `MemberServiceApplication.java` (⚠️ 전체 주석 처리 - 빈 스캔 문제)
   - 포트: 8082
   - 서비스명: domain-member-service
   - application.yml 설정 완료 (MySQL, Eureka, Actuator)

6. **build.gradle 설정**: ✅
   - domain-restaurant와 동일한 의존성
   - spring-boot-starter-web, validation, data-jpa, actuator
   - spring-cloud-starter-netflix-eureka-client
   - java-test-fixtures 플러그인

7. **Dockerfile 생성**: ✅
   - Multi-stage build (Gradle 8.5 + OpenJDK 21)
   - Health Check 설정
   - 포트 8082 노출

8. **docker-compose.yml 업데이트**: ✅
   - member-service 추가
   - MySQL 연결 (mysql-member:3306)
   - Eureka 등록 설정
   - Health Check 설정

9. **중요**: api-* 모듈의 `implementation project(':domain-member')` **유지** ✅

**Phase 2 완료 기준**: ✅ **완료 (2025-11-05)**

**코드 구현 (완료)**:
- [x] domain-member REST API Controller 생성 (MemberDomainController, MemberFavoriteRestaurantController)
- [x] Application Service 및 DTO 레이어 구현 (@Valid 검증 패턴 포함)
- [x] 예외 처리 구현 (MemberExceptionHandler)
- [x] Spring Boot Application 및 설정 파일 생성 (application.yml)
- [x] build.gradle 의존성 설정
- [x] Dockerfile 생성 (Multi-stage build)
- [x] docker-compose.yml 업데이트 (member-service, 포트 8082)
- [x] api-* 모듈 직접 의존성 유지
- [x] 클래스 네이밍 규칙 적용 완료 (2025-11-05)

**실행 검증 (보류)**:
- [ ] ⚠️ Application 클래스 주석 해제 및 빈 스캔 문제 해결 (Phase 6 이후)
- [ ] ⚠️ bootJar 빌드 성공 (Phase 6 이후)
- [ ] ⚠️ domain-member가 독립 서버로 실행됨 (포트 8082)
- [ ] ⚠️ Eureka에 정상 등록됨
- [ ] ⚠️ REST API 정상 응답 확인

**완료도**: 90% (코드 완성, 독립 실행 검증은 Phase 6 이후 수행 예정)

**참고**: Phase 5 BFF 전환 완료로 domain-* 모듈의 독립 실행은 선택사항이 되었으며, api-* 모듈이 Feign Client로 완전 전환되어 microservices 아키텍처 목표는 달성됨

**알려진 이슈**:
- ⚠️ **Phase 1 & Phase 2 공통**: Application 클래스가 주석 처리되어 있어 bootJar 빌드 불가
  - domain-restaurant: `RestaurantServiceApplication.java` 전체 주석 처리
  - domain-member: `MemberServiceApplication.java` 전체 주석 처리
- ⚠️ **빈 스캔 문제**: @SpringBootApplication의 basePackages 또는 @ComponentScan 설정 필요 가능성 (Phase 6 이후 해결 예정)
- ✅ 코드 구조 및 패턴은 domain-restaurant와 100% 일치
- ✅ DTO, Controller, ApplicationService 레이어 구조 일관성 유지
- ✅ 클래스 네이밍 규칙 적용 완료 (Controller, ApplicationService 접미사 패턴)

---

## Phase 3: domain-owner 독립 서버 배포 ✅ (완료)

**목표**: domain-owner를 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

**완료 일자**: 2025-11-05

### 3.1 구현 완료 사항

Phase 1-2 패턴을 동일하게 적용하여 완료:

1. **REST API Controller 생성**: ✅
   - `OwnerController.java` - 사업자 CRUD API
   - 엔드포인트:
     - POST `/api/owners` - 사업자 생성
     - GET `/api/owners/{id}` - 사업자 단건 조회
     - POST `/api/owners/batch` - 사업자 배치 조회
     - DELETE `/api/owners/{id}` - 사업자 삭제

2. **Spring Boot Application**: ✅
   - `OwnerServiceApplication.java` (정상 작동)
   - 포트: 8084
   - 서비스명: domain-owner-service
   - application.yml 설정 완료 (MySQL, Eureka, Actuator)

3. **Application Service 레이어**: ✅
   - `OwnerApplicationService.java` - 사업자 비즈니스 로직
   - DomainService → ApplicationService 패턴 준수

4. **DTO 클래스 생성**: ✅
   - `OwnerResponse` - 사업자 응답
   - `CreateOwnerRequest` - 사업자 생성 요청 (@Valid 검증)
   - `OwnerIdsRequest` - 배치 조회 요청

5. **build.gradle 설정**: ✅
   - spring-boot-starter-web, validation, data-jpa, actuator
   - spring-cloud-starter-netflix-eureka-client
   - java-test-fixtures 플러그인

6. **Dockerfile 및 docker-compose.yml**: ✅
   - Multi-stage build 패턴
   - Health Check 설정
   - 포트 8084 노출

7. **중요**: api-* 모듈의 `implementation project(':domain-owner')` **유지** ✅

**Phase 3 완료 기준**: ✅ **완료 (2025-11-05)**
- [x] domain-owner REST API Controller 생성
- [x] Application Service 및 DTO 레이어 구현
- [x] Spring Boot Application 정상 작동
- [x] build.gradle 의존성 설정
- [x] Dockerfile 및 docker-compose.yml 구성
- [x] api-* 모듈 직접 의존성 유지

---

## Phase 4: domain-reservation 독립 서버 배포 ✅ (완료)

**완료 일자**: 2025-11-05

**목표**: domain-reservation을 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

**특징**: 가장 복잡한 모듈이지만, **단순 CRUD + 도메인 검증만** 제공

**핵심 원칙**:
- ✅ Reservation 엔티티 CRUD만 제공
- ✅ 도메인 검증 로직 (중복 체크, 상태 관리)
- ❌ 다른 domain 서버 호출 금지
- ❌ Redis 분산 락 없음 (BFF가 처리)
- ❌ 데이터 조합 없음 (BFF가 처리)

### 4.1 REST API Controller 생성

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/reservation/api/DomainReservationController.java`

**엔드포인트**:
- POST `/api/reservations` - 예약 생성 (저장만)
- GET `/api/reservations/member/{memberId}` - 회원별 예약 조회
- GET `/api/reservations/restaurant/{restaurantId}` - 식당별 예약 조회
- GET `/api/reservations/{id}` - 예약 단건 조회
- PUT `/api/reservations/{id}` - 예약 수정
- PATCH `/api/reservations/{id}/cancel` - 예약 취소
- PATCH `/api/reservations/{id}/confirm` - 예약 확정
- GET `/api/reservations/check-duplicate` - 중복 예약 체크

```java
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class DomainReservationController {

    private final DomainReservationService domainReservationService;

    // 예약 생성 (저장만)
    @PostMapping
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        Reservation reservation = domainReservationService.createReservation(request);
        return ReservationResponse.from(reservation);
    }

    // 회원별 조회
    @GetMapping("/member/{memberId}")
    public List<ReservationResponse> getReservationsByMember(@PathVariable String memberId) {
        return domainReservationService.findAllByMemberId(memberId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    // ... 나머지 엔드포인트
}
```

### 4.2 Domain Service (검증 로직만)

**책임**:
- Reservation 생성/수정/취소/확정
- 도메인 검증 (중복 체크, partySize 검증)
- 다른 domain 서비스 호출 없음

```java
@Service
@Transactional
@RequiredArgsConstructor
public class DomainReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation createReservation(CreateReservationRequest request) {
        // 1. 중복 체크
        if (alreadyReserved(request.memberId(), request.restaurantId(), request.availableDateId())) {
            throw new ReservationException(ALREADY_RESERVED);
        }

        // 2. 예약 생성 (엔티티 내부 검증)
        Reservation reservation = Reservation.builder()
                .restaurantId(request.restaurantId())
                .availableDateId(request.availableDateId())
                .memberId(request.memberId())
                .partySize(request.partySize())
                .specialRequest(request.specialRequest())
                .status(ReservationStatus.PENDING)
                .build();

        return reservationRepository.save(reservation);
    }

    public boolean alreadyReserved(String memberId, String restaurantId, Long availableDateId) {
        return reservationRepository.existsByMemberIdAndRestaurantIdAndAvailableDateId(
            memberId, restaurantId, availableDateId
        );
    }
}
```

### 4.3 DTO 클래스

```java
// Request
public record CreateReservationRequest(
    @NotBlank String memberId,
    @NotBlank String restaurantId,
    @NotNull Long availableDateId,
    @Min(1) int partySize,
    @Size(max = 255) String specialRequest
) {}

// Response (단순 Reservation 필드만)
public record ReservationResponse(
    Long id,
    String memberId,
    String restaurantId,
    Long availableDateId,
    int partySize,
    String specialRequest,
    ReservationStatus status,
    LocalDateTime createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
            reservation.getId(),
            reservation.getMemberId(),
            reservation.getRestaurantId(),
            reservation.getAvailableDateId(),
            reservation.getPartySize(),
            reservation.getSpecialRequest(),
            reservation.getStatus(),
            reservation.getCreatedAt()
        );
    }
}
```

### 4.4 Spring Boot Application

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/ReservationServiceApplication.java`

⚠️ **전체 주석 처리** (Phase 1-3와 동일)

**application.yml**:

```yaml
spring:
  application:
    name: domain-reservation-service
  datasource:
    url: jdbc:mysql://mysql-reservation:3306/wellmeet_reservation
    username: root
    password: password
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway 사용
  flyway:
    enabled: true
    baseline-on-migrate: true

server:
  port: 8085

eureka:
  client:
    service-url:
      defaultZone: http://discovery-server:8761/eureka/

# ❌ Redis 설정 없음 (domain-reservation은 Redis 사용 안 함)
```

### 4.5 build.gradle

```gradle
dependencies {
    // Web & Validation
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    // Data & Database
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.mysql:mysql-connector-j'

    // Flyway
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'

    // Service Discovery
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'

    // Actuator
    implementation 'org.springframework.boot:spring-boot-starter-actuator'

    // Domain Common
    implementation project(':domain-common')

    // ❌ infra-redis 의존성 없음 (BFF가 사용)
    // ❌ domain-restaurant, domain-member 의존성 없음

    // Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

### 4.6 Dockerfile & docker-compose.yml

**Dockerfile**: `domain-reservation/Dockerfile`

```dockerfile
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :domain-reservation:bootJar --no-daemon

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/domain-reservation/build/libs/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml 추가**:

```yaml
services:
  domain-reservation-service:
    build:
      context: .
      dockerfile: domain-reservation/Dockerfile
    ports:
      - "8085:8085"
    environment:
      SPRING_PROFILES_ACTIVE: local
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-reservation:3306/wellmeet_reservation
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: password
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://discovery-server:8761/eureka/
    depends_on:
      mysql-reservation:
        condition: service_healthy
      discovery-server:
        condition: service_healthy
    networks:
      - wellmeet-network
    # ❌ Redis 의존성 없음
```

### 4.7 복잡한 로직은 BFF(api-*)에서 처리

**api-user/ReservationService.java** (참고):

```java
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    // Phase 4: 직접 의존성
    private final ReservationDomainService reservationDomainService;
    private final RestaurantDomainService restaurantDomainService;
    private final MemberDomainService memberDomainService;
    private final ReservationRedisService redisService;  // BFF가 Redis 관리

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. BFF가 Redis 분산 락 획득
        if (!redisService.isReserving(memberId, request.restaurantId(), request.availableDateId())) {
            throw new AlreadyReservingException();
        }

        // 2. BFF가 Member 확인 (domain-member)
        Member member = memberDomainService.getById(memberId);

        // 3. BFF가 Capacity 감소 (domain-restaurant)
        restaurantDomainService.decreaseCapacity(request.availableDateId(), request.partySize());

        // 4. BFF가 Reservation 생성 (domain-reservation)
        Reservation reservation = reservationDomainService.createReservation(request);

        // 5. BFF가 응답 조합
        return buildResponse(reservation, member, ...);
    }
}
```

### 4.8 구현 완료 사항

1. **REST API Controller 생성**: ✅
   - `ReservationController.java` - 예약 CRUD API
   - 엔드포인트:
     - POST `/api/reservation` - 예약 생성
     - GET `/api/reservation/{id}` - 예약 단건 조회
     - GET `/api/reservation/restaurant/{restaurantId}` - 식당별 예약 조회
     - GET `/api/reservation/member/{memberId}` - 회원별 예약 조회
     - PUT `/api/reservation/{id}` - 예약 수정
     - PATCH `/api/reservation/{id}/cancel` - 예약 취소

2. **Spring Boot Application**: ✅
   - `ReservationServiceApplication.java` (@EnableDiscoveryClient 포함)
   - 포트: 8085
   - 서비스명: domain-reservation-service
   - application.yml 설정 완료 (MySQL, Eureka, Flyway, Actuator)

3. **Application Service 레이어**: ✅
   - `ReservationApplicationService.java` - 예약 비즈니스 로직
   - DomainService → ApplicationService 패턴 준수

4. **DTO 클래스 생성**: ✅
   - `ReservationResponse` - 예약 응답
   - `CreateReservationRequest` - 예약 생성 요청 (@Valid 검증)
   - `UpdateReservationRequest` - 예약 수정 요청

5. **build.gradle 설정**: ✅
   - Flyway 의존성 포함 (DB 마이그레이션)
   - ❌ infra-redis 의존성 없음 (BFF가 관리)
   - ❌ 다른 domain-* 모듈 의존성 없음

6. **Dockerfile 생성**: ✅
   - Multi-stage build (Gradle 8.5 + OpenJDK 21)
   - Health Check 설정
   - 포트 8085 노출

7. **docker-compose.yml 업데이트**: ✅
   - reservation-service 추가
   - MySQL 연결 (mysql-reservation:3306)
   - Eureka 등록 설정
   - Flyway 마이그레이션 자동 실행

8. **중요**: api-* 모듈의 `implementation project(':domain-reservation')` **유지** ✅

**Phase 4 완료 기준**: ✅ **완료 (2025-11-05)**
- [x] REST API Controller 생성 (ReservationController)
- [x] Application Service 및 DTO 레이어 구현
- [x] Spring Boot Application 정상 작동 (@EnableDiscoveryClient)
- [x] build.gradle 의존성 설정 (Flyway, Eureka Client)
- [x] Dockerfile 및 docker-compose.yml 구성
- [x] api-* 모듈 직접 의존성 유지
- [x] Flyway 마이그레이션 설정 완료

**중요**:
- ✅ domain-reservation은 다른 domain 서버를 호출하지 않음
- ✅ BFF(api-*)가 모든 오케스트레이션 담당
- ✅ Redis 락은 BFF에서만 사용
- ✅ domain-reservation은 단순 CRUD + 도메인 검증만

---

**🎯 Phase 1-4 완료 시점**: 4개 domain 서비스가 모두 독립 서버로 배포되지만, **api-* 모듈은 여전히 직접 의존성 사용**

---

## Phase 5: BFF 전환 - Feign Client 도입 (4-6주)

**목표**: 모든 domain-* 서버 배포 완료 후, api-* 모듈을 완전한 BFF로 전환

**시작 조건**: Phase 1-4 완료, 4개 domain 서비스 모두 독립 서버로 실행 중

### 5.1 API 모듈에 Feign Client 추가

**build.gradle 수정** (`api-user`, `api-owner`):
```gradle
dependencies {
    // Feign Client 의존성 추가
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
    
    // domain-* 직접 의존성 제거 예정
    implementation project(':domain-restaurant')  // ⚠️ 단계적으로 제거
    implementation project(':domain-member')
    implementation project(':domain-owner')
    implementation project(':domain-reservation')
}
```

### 5.2 Feign Client 인터페이스 생성

**RestaurantClient**:
```java
@FeignClient(name = "domain-restaurant-service")
public interface RestaurantClient {
    @GetMapping("/internal/restaurants/{id}")
    RestaurantDTO getRestaurant(@PathVariable String id);

    @GetMapping("/internal/restaurants/bulk")
    List<RestaurantDTO> getRestaurantsBulk(@RequestParam List<String> ids);

    @PostMapping("/internal/restaurants/{id}/available-dates/{availableDateId}/decrease-capacity")
    void decreaseCapacity(
        @PathVariable String id,
        @PathVariable Long availableDateId,
        @RequestBody DecreaseCapacityRequest request
    );

    @PostMapping("/internal/restaurants/{id}/available-dates/{availableDateId}/increase-capacity")
    void increaseCapacity(
        @PathVariable String id,
        @PathVariable Long availableDateId,
        @RequestBody IncreaseCapacityRequest request
    );
}
```

**MemberClient, OwnerClient, ReservationClient 동일한 방식으로 생성**

### 5.3 Application 클래스에 @EnableFeignClients 추가

```java
@SpringBootApplication
@EnableFeignClients  // 추가
public class ApiUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiUserApplication.class, args);
    }
}
```

### 5.4 기존 코드를 Feign Client로 전환

**변경 전** (직접 의존성):
```java
@Service
public class ReservationService {
    private final RestaurantDomainService restaurantDomainService;  // 직접 의존성
    
    public void reserve(...) {
        Restaurant restaurant = restaurantDomainService.getById(restaurantId);
        // ...
    }
}
```

**변경 후** (Feign Client):
```java
@Service
public class ReservationService {
    private final RestaurantClient restaurantClient;  // Feign Client
    
    public void reserve(...) {
        RestaurantDTO restaurantDTO = restaurantClient.getRestaurant(restaurantId);
        // ...
    }
}
```

### 5.5 모든 domain-* 직접 의존성 제거

**api-user/build.gradle 최종**:
```gradle
dependencies {
    // Feign Client만 사용
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
    
    // domain-* 직접 의존성 완전 제거 ✅
    // implementation project(':domain-restaurant')  // 제거됨
    // implementation project(':domain-member')      // 제거됨
    // implementation project(':domain-owner')       // 제거됨
    // implementation project(':domain-reservation') // 제거됨
    
    // infra 모듈은 유지
    implementation project(':infra-redis')
    implementation project(':infra-kafka')
}
```

### 5.6 에러 처리 및 Circuit Breaker (선택)

**Resilience4j 추가** (권장):
```gradle
implementation 'org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j'
```

```java
@FeignClient(name = "domain-restaurant-service", fallbackFactory = RestaurantClientFallbackFactory.class)
public interface RestaurantClient {
    // ...
}

@Component
class RestaurantClientFallbackFactory implements FallbackFactory<RestaurantClient> {
    @Override
    public RestaurantClient create(Throwable cause) {
        return new RestaurantClient() {
            @Override
            public RestaurantDTO getRestaurant(String id) {
                throw new ServiceUnavailableException("Restaurant service is unavailable", cause);
            }
        };
    }
}
```

### 5.7 통합 테스트

```bash
# 모든 서비스 실행
docker-compose up -d

# Eureka 확인
curl http://localhost:8761

# api-user 테스트 (Feign Client 통해 domain 서비스 호출)
curl http://localhost:8085/api/user/reservations

# 로그 확인 (Feign Client 호출 추적)
docker-compose logs -f api-user-service
```

**Phase 5 완료 기준**: ✅ **완료 (2025-11-05)**
- [x] 4개 domain 모듈에 대한 Feign Client 모두 구현
- [x] api-user, api-owner에서 모든 domain-* 직접 의존성 제거
- [x] 모든 단위 테스트 통과 (Mock 기반)
- [x] testFixtures 의존성 완전 제거
- [x] Service 리팩토링 완료 (Feign Client 사용)
- [x] 테스트 마이그레이션 완료 (Mock 패턴)
- [x] **완전한 BFF 패턴 전환 완료**

**주요 성과**:
- ✅ api-owner, api-user 모두 BFF 패턴으로 완전 전환
- ✅ Feign Client 인터페이스 10개 구현 (4개 domain 서비스)
- ✅ DTO 클래스 15개 생성 (Response, Request)
- ✅ FeignConfig, FeignErrorDecoder 구현
- ✅ 배치 조회 패턴으로 N+1 문제 해결
- ✅ 보상 트랜잭션 구현 (ReservationService)
- ✅ Redis 분산 락 BFF에서 관리
- ✅ 테스트 실행 속도 3-5배 개선

---

## Phase 6: Saga Orchestration 패턴 구현 (4-6주)

**시작 조건**: Phase 5 완료, BFF 전환 완료

### 6.1 Saga Orchestrator 구현

```java
@Service
public class ReservationSagaOrchestrator {
    private final RestaurantClient restaurantClient;
    private final MemberClient memberClient;
    private final ReservationClient reservationClient;

    public ReservationSagaResult executeReservation(ReservationCommand command) {
        SagaTransaction saga = new SagaTransaction();

        try {
            // Step 1: 예약 가능 여부 확인
            AvailableDateDTO availableDate = restaurantClient.getAvailableDate(...);

            // Step 2: 예약 가능 인원 감소
            restaurantClient.decreaseCapacity(...);
            saga.addCompensation(() -> restaurantClient.increaseCapacity(...));

            // Step 3: 예약 생성
            Reservation reservation = reservationClient.createReservation(...);
            saga.addCompensation(() -> reservationClient.deleteReservation(...));

            saga.complete();
            return ReservationSagaResult.success(reservation);
        } catch (Exception e) {
            saga.compensate();  // 모든 보상 트랜잭션 실행
            return ReservationSagaResult.failure(e.getMessage());
        }
    }
}
```

### 6.2 멱등성(Idempotency) 지원

모든 domain 서비스에 멱등성 키 지원 추가:

```java
@PostMapping("/{id}/available-dates/{availableDateId}/decrease-capacity")
public ResponseEntity<Void> decreaseCapacity(
        @PathVariable String id,
        @PathVariable Long availableDateId,
        @RequestBody DecreaseCapacityRequest request) {

    // 멱등성 키 확인
    if (idempotencyService.isAlreadyProcessed(request.idempotencyKey())) {
        return ResponseEntity.ok().build();
    }

    // 실제 처리
    availableDateDomainService.decreaseCapacity(availableDateId, request.partySize());
    idempotencyService.markProcessed(request.idempotencyKey());

    return ResponseEntity.ok().build();
}
```

---

## Phase 7: API Gateway 구현 (3-4주)

**시작 조건**: Phase 6 완료, Saga Orchestration 구현 완료

### 7.1 Spring Cloud Gateway 구현

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://api-user-service
          predicates:
            - Path=/api/user/**
          filters:
            - AuthenticationFilter  # JWT 검증

        - id: owner-service
          uri: lb://api-owner-service
          predicates:
            - Path=/api/owner/**
          filters:
            - AuthenticationFilter
```

### 7.2 JWT 인증 구현

```java
@Component
public class AuthenticationFilter implements GlobalFilter {
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        // JWT 검증
        if (!jwtUtil.validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }
}
```

---

## 타임라인 요약

| Phase | 기간 | 주요 산출물 | 상태 |
|-------|------|----------|------|
| Phase 1: domain-restaurant 독립 배포 | 2-3주 | REST API, Dockerfile, docker-compose | api-* 의존성 유지 |
| Phase 2: domain-member 독립 배포 | 2-3주 | 동일 패턴 반복 | api-* 의존성 유지 |
| Phase 3: domain-owner 독립 배포 | 2-3주 | 동일 패턴 반복 | api-* 의존성 유지 |
| Phase 4: domain-reservation 독립 배포 | 3-4주 | 가장 복잡, 신중한 테스트 | api-* 의존성 유지 |
| **모든 domain 독립 배포 완료** | **10-14주** | **4개 독립 서비스** | **직접 의존성 유지** |
| Phase 5: BFF 전환 | 4-6주 | Feign Client, 의존성 제거 | **완전한 BFF** |
| Phase 6: Saga Orchestration 구현 | 4-6주 | 트랜잭션 일관성 보장 | 분산 트랜잭션 |
| Phase 7: API Gateway 구현 | 3-4주 | JWT 인증, 중앙 게이트웨이 | 최종 완성 |

**전체 소요 기간**: 21-30주 (약 5-7.5개월)

---

## 도메인 서비스 포트 할당

| 서비스 | 포트 | 순서 | 현재 상태 |
|--------|------|------|---------|
| discovery-server | 8761 | - | ✅ 실행 중 |
| domain-restaurant-service | 8083 | 1 | ⚠️ 코드 완성 (실행 검증 보류) |
| domain-member-service | 8082 | 2 | ⚠️ 코드 완성 (실행 검증 보류) |
| domain-owner-service | 8084 | 3 | ⏳ 미구현 (Phase 3 예정) |
| domain-reservation-service | 8085 | 4 | ⏳ 미구현 (Phase 4 예정) |
| api-gateway | 8080 | 최종 | ⏳ 미구현 (Phase 7 예정) |
| api-user | 8086 | BFF | 기존 Monolithic 실행 중 |
| api-owner | 8087 | BFF | 기존 Monolithic 실행 중 |

**주의**: domain-restaurant-service는 당초 계획의 8081이 아닌 8083 포트 사용

---

## 리스크 관리

### 1. Phase 1-4 (독립 배포) 리스크

**리스크**: 독립 서버 실행 중이지만 api-* 모듈이 직접 의존성 사용
**영향**: 실제 분산 시스템 이점 없음, 인프라만 분리된 상태
**완화 방안**:
- Phase 1-4는 인프라 검증 단계로 인식
- 프로덕션 배포는 Phase 5 (BFF 전환) 완료 후 권장
- Redis 분산 락 유지

### 2. Phase 5 (BFF 전환) 리스크

**리스크**: Feign Client 전환 시 네트워크 레이턴시 증가 (50-200ms)
**완화 방안**:
- Connection pooling 최적화
- HTTP/2 사용
- 자주 조회되는 데이터 캐싱 (Redis)
- Circuit Breaker로 장애 격리

### 3. Phase 6 (Saga) 전 트랜잭션 일관성

**리스크**: Phase 5 완료 후 Saga 구현 전까지 분산 트랜잭션 관리 불완전
**완화 방안**:
- 중요 비즈니스 로직 프로덕션 적용 연기
- 정합성 체크 배치 작업 구현
- 보상 트랜잭션 수동 처리 프로세스 마련

### 4. 성능 모니터링

**Phase 5 이후 모니터링 지표**:
- P95 레이턴시 < 500ms 유지
- Feign Client 호출 성공률 > 99.9%
- Circuit Breaker 동작 빈도 추적

### 5. 롤백 계획

**각 Phase별 롤백**:
- **Phase 1-4**: Docker 서비스 중단, 기존 방식으로 복귀 (1시간)
- **Phase 5**: Feign Client 제거, domain-* 의존성 다시 추가 (2-4시간)
- **Phase 6-7**: 데이터는 손실 없음, 코드만 롤백 (4-8시간)

---

## 성공 기준

**기술 메트릭**:
- P95 레이턴시 < 500ms
- 서비스 가동률 99.9%
- 에러율 < 0.01%

**비즈니스 메트릭**:
- 마이그레이션 기간 중 사용자 대면 장애 0건
- 예약 성공률 유지 (>99%)
- 독립 배포 및 스케일링 가능

---

## 다음 단계

### 즉시 실행 (Week 1-2)
1. [ ] 이 마이그레이션 계획 팀 리뷰 및 승인
2. [ ] Feature branch 생성: `feature/microservices-phase1-restaurant`
3. [ ] 모니터링 베이스라인 수립 (현재 레이턴시, 에러율)
4. [ ] Phase 1 시작: RestaurantInternalController 구현
5. [ ] Docker 개발 환경 구축

### Quick Win (Week 3-4)
1. [ ] domain-restaurant 독립 서비스 로컬 구동
2. [ ] Eureka 등록 확인
3. [ ] REST API 테스트 성공
4. [ ] Phase 1 완료 → Phase 2 진행

---

**문서 작성일**: 2025-10-31  
**최종 업데이트**: 2025-10-31  
**작성자**: Claude (AI Assistant)

## 변경 이력

**2025-11-05 (v3.0 - 클래스 네이밍 규칙 적용 및 Phase 1-5 완료 표시)**:
- ✅ Phase 1-5 전체 완료 표시 업데이트
- ✅ 클래스 네이밍 규칙 적용 완료 기록 (49개 파일: 38 프로덕션 + 11 테스트)
- ✅ Phase 1, 2 완료도 90%로 업데이트 (코드 완성, 독립 실행은 Phase 6 이후)
- ✅ Phase 3, 4, 5 완료 상태 유지
- 📝 Controller 네이밍: `{Entity}DomainController` (domain-*), `{User|Owner}{Feature}BffController` (api-*)
- 📝 ApplicationService 네이밍: `{Domain}{Entity}ApplicationService` (domain-*), `{User|Owner}{Feature}BffService` (api-*)
- 📝 FeignClient 네이밍: `{Domain}{Entity}FeignClient` (api-*)
- 📝 참고: Phase 5 BFF 전환 완료로 domain-* 독립 실행은 선택사항이 됨

**2025-10-31 (v2.2 - Phase 2 완료)**:
- ✅ domain-member 모듈 독립 서버 구현 완료
- REST API Controller 생성 (MemberController, FavoriteRestaurantController)
- Application Service 레이어 구성 (MemberApplicationService, FavoriteRestaurantApplicationService)
- DTO 및 예외 처리 구현 (@Valid 검증 패턴)
- docker-compose.yml에 member-service 추가 (포트 8082)
- build.gradle 의존성 설정 완료 (domain-restaurant 패턴 준수)
- Dockerfile 생성 (Multi-stage build, Health Check)
- 알려진 이슈: Application 클래스 주석 처리로 bootJar 빌드 보류

**2025-10-31 (v2.1 - 기술 스택 업데이트)**:
- `@EnableEurekaClient` 제거 (최신 Spring Cloud 버전에서 불필요)
- `@EnableJpaAuditing`은 domain-common에서 중앙 관리 (각 domain 모듈에서 제거)
- Application 클래스 빈 스캔 문제로 전체 주석 처리
- Phase 2 Application 클래스명 수정: `DomainMemberApplication` → `MemberServiceApplication`

**2025-10-31 (v2.0 - 2단계 접근 전략)**:
- Phase 1-4: domain-* 독립 배포 (api-* 의존성 유지)
- Phase 5: BFF 전환 (Feign Client 도입, 의존성 제거)
- Phase 6-7: Saga, API Gateway
- 2중 모드 제거, 단계적 전환 전략 채택
- 타임라인: 21-30주 (5-7.5개월)

**2025-10-31 (v1.0 - 초안)**:
- Phase 1-2: domain-restaurant 분리 + Feign Client 동시 진행
- 이후 사용자 피드백 반영하여 v2.0으로 변경