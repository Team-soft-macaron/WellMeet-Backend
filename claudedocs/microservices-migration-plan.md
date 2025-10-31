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

**Phase 1 완료 기준**:
- [ ] domain-restaurant가 독립 서버로 실행됨 (포트 8081)
- [ ] Eureka에 정상 등록됨
- [ ] `/internal/restaurants/*` REST API 응답 확인
- [ ] Health check 정상 작동
- [ ] **api-* 모듈은 여전히 직접 의존성 사용** (변경 없음)

---

## Phase 2: domain-member 독립 서버 배포 (2-3주)

**목표**: domain-member를 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

### 2.1 동일한 패턴 반복

Phase 1과 동일한 방식으로 진행:

1. **REST API Controller 생성**: `MemberInternalController`
   - 회원 조회, 생성, 수정 API
   - 즐겨찾기 관련 API

2. **Spring Boot Application**: `MemberServiceApplication`
   - 포트: 8082
   - 서비스명: domain-member-service
   - ⚠️ **전체 주석 처리** (빈 스캔 문제)

3. **Dockerfile 생성**: `domain-member/Dockerfile`

4. **docker-compose.yml 업데이트**: domain-member-service 추가

5. **검증**: 독립 실행, Eureka 등록, API 테스트

6. **중요**: api-* 모듈의 `implementation project(':domain-member')` **유지**

**Phase 2 완료 기준**:
- [ ] domain-member 독립 서버 실행 (포트 8082)
- [ ] Eureka 등록 확인
- [ ] REST API 정상 응답
- [ ] api-* 모듈 직접 의존성 유지

---

## Phase 3: domain-owner 독립 서버 배포 (2-3주)

**목표**: domain-owner를 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

### 3.1 동일한 패턴 반복

1. **REST API Controller 생성**: `OwnerInternalController`
   - 사업자 조회, 생성, 수정 API

2. **Spring Boot Application**: `DomainOwnerApplication`
   - 포트: 8083
   - 서비스명: domain-owner-service

3. **Dockerfile 생성**: `domain-owner/Dockerfile`

4. **docker-compose.yml 업데이트**: domain-owner-service 추가

5. **검증**: 독립 실행, Eureka 등록, API 테스트

6. **중요**: api-* 모듈의 `implementation project(':domain-owner')` **유지**

**Phase 3 완료 기준**:
- [ ] domain-owner 독립 서버 실행 (포트 8083)
- [ ] Eureka 등록 확인
- [ ] REST API 정상 응답
- [ ] api-* 모듈 직접 의존성 유지

---

## Phase 4: domain-reservation 독립 서버 배포 (3-4주)

**목표**: domain-reservation을 독립 서버로 배포하되, **api-* 모듈은 직접 의존성 유지**

**특징**: 가장 복잡한 모듈 (모든 도메인과 연관)

### 4.1 동일한 패턴 반복

1. **REST API Controller 생성**: `ReservationInternalController`
   - 예약 조회, 생성, 수정, 취소 API
   - 가장 많은 API 엔드포인트

2. **Spring Boot Application**: `DomainReservationApplication`
   - 포트: 8084
   - 서비스명: domain-reservation-service

3. **Dockerfile 생성**: `domain-reservation/Dockerfile`

4. **docker-compose.yml 업데이트**: domain-reservation-service 추가

5. **추가 고려사항**:
   - Flyway 마이그레이션 유지
   - 예약 생성 로직 복잡 → 신중한 테스트 필요
   - Redis 분산 락 통합 확인

6. **중요**: api-* 모듈의 `implementation project(':domain-reservation')` **유지**

**Phase 4 완료 기준**:
- [ ] domain-reservation 독립 서버 실행 (포트 8084)
- [ ] Eureka 등록 확인
- [ ] REST API 정상 응답
- [ ] Flyway 마이그레이션 정상 작동
- [ ] Redis 분산 락 정상 작동
- [ ] api-* 모듈 직접 의존성 유지

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

**Phase 5 완료 기준**:
- [ ] 4개 domain 모듈에 대한 Feign Client 모두 구현
- [ ] api-user, api-owner에서 모든 domain-* 직접 의존성 제거
- [ ] 모든 통합 테스트 통과
- [ ] 로컬 환경에서 Feign Client 통신 정상 작동
- [ ] Circuit Breaker 정상 작동 (선택)
- [ ] **완전한 BFF 패턴 전환 완료**

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

| 서비스 | 포트 | 순서 |
|--------|------|------|
| discovery-server | 8761 | - |
| domain-restaurant-service | 8081 | 1 |
| domain-member-service | 8082 | 2 |
| domain-owner-service | 8083 | 3 |
| domain-reservation-service | 8084 | 4 |
| api-gateway | 8080 | 최종 |
| api-user | 8085 | BFF |
| api-owner | 8086 | BFF |

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