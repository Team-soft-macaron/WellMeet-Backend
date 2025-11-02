# Phase 4: domain-reservation 독립 서버 배포 상세 계획

**작성일**: 2025-11-02
**목표**: domain-reservation을 독립 서버로 배포 (api-* 직접 의존성 유지)
**예상 기간**: 3-4주
**복잡도**: ⚠️ HIGH (가장 복잡한 모듈이지만 단순 CRUD로 제한)

---

## 📋 목차

1. [설계 원칙](#설계-원칙)
2. [구현 단계](#구현-단계)
3. [코드 예시](#코드-예시)
4. [BFF 역할](#bff-역할)
5. [검증 계획](#검증-계획)
6. [알려진 이슈](#알려진-이슈)
7. [타임라인](#타임라인)

---

## 설계 원칙

### domain-reservation의 책임 (제한적)

✅ **제공하는 것**:
- Reservation 엔티티 CRUD
- 도메인 검증 (중복 체크, partySize 검증)
- 상태 변경 (cancel, confirm)
- Flyway 마이그레이션 관리

❌ **제공하지 않는 것**:
- 다른 domain 서버 호출 (restaurant, member)
- Redis 분산 락 관리
- AvailableDate capacity 관리
- 데이터 조합 및 응답 생성

### BFF(api-*)의 책임

✅ **BFF가 담당**:
- Redis 분산 락 획득/해제
- 여러 domain 서비스 오케스트레이션
- domain-member 호출 (회원 확인)
- domain-restaurant 호출 (capacity 관리)
- domain-reservation 호출 (예약 생성)
- 응답 데이터 조합
- 이벤트 발행 (Kafka)

---

## 구현 단계

### Step 1: REST API Controller

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/reservation/api/DomainReservationController.java`

**엔드포인트 설계**:

| Method | Path | 설명 | 책임 |
|--------|------|------|------|
| POST | `/api/reservations` | 예약 생성 | 저장만 |
| GET | `/api/reservations/member/{memberId}` | 회원별 조회 | 단순 조회 |
| GET | `/api/reservations/restaurant/{restaurantId}` | 식당별 조회 | 단순 조회 |
| GET | `/api/reservations/{id}` | 단건 조회 | 단순 조회 |
| PUT | `/api/reservations/{id}` | 예약 수정 | 업데이트만 |
| PATCH | `/api/reservations/{id}/cancel` | 예약 취소 | 상태 변경만 |
| PATCH | `/api/reservations/{id}/confirm` | 예약 확정 | 상태 변경만 |
| GET | `/api/reservations/check-duplicate` | 중복 체크 | 검증만 |

**구현 코드**:

```java
package com.wellmeet.domain.reservation.api;

import com.wellmeet.domain.reservation.api.dto.*;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.service.DomainReservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class DomainReservationController {

    private final DomainReservationService domainReservationService;

    @PostMapping
    public ReservationResponse createReservation(@Valid @RequestBody CreateReservationRequest request) {
        Reservation reservation = domainReservationService.createReservation(request);
        return ReservationResponse.from(reservation);
    }

    @GetMapping("/member/{memberId}")
    public List<ReservationResponse> getReservationsByMember(@PathVariable String memberId) {
        return domainReservationService.findAllByMemberId(memberId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/restaurant/{restaurantId}")
    public List<ReservationResponse> getReservationsByRestaurant(@PathVariable String restaurantId) {
        return domainReservationService.findAllByRestaurantId(restaurantId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservation(
        @PathVariable Long reservationId,
        @RequestParam String memberId
    ) {
        Reservation reservation = domainReservationService.getByIdAndMemberId(reservationId, memberId);
        return ReservationResponse.from(reservation);
    }

    @PutMapping("/{reservationId}")
    public ReservationResponse updateReservation(
        @PathVariable Long reservationId,
        @RequestParam String memberId,
        @Valid @RequestBody UpdateReservationRequest request
    ) {
        Reservation reservation = domainReservationService.updateReservation(
            reservationId,
            memberId,
            request
        );
        return ReservationResponse.from(reservation);
    }

    @PatchMapping("/{reservationId}/cancel")
    public void cancelReservation(
        @PathVariable Long reservationId,
        @RequestParam String memberId
    ) {
        domainReservationService.cancelReservation(reservationId, memberId);
    }

    @PatchMapping("/{reservationId}/confirm")
    public void confirmReservation(@PathVariable Long reservationId) {
        domainReservationService.confirmReservation(reservationId);
    }

    @GetMapping("/check-duplicate")
    public boolean isDuplicate(
        @RequestParam String memberId,
        @RequestParam String restaurantId,
        @RequestParam Long availableDateId
    ) {
        return domainReservationService.alreadyReserved(memberId, restaurantId, availableDateId);
    }
}
```

---

### Step 2: Domain Service

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/reservation/service/DomainReservationService.java`

**책임**:
- Reservation 생성/수정/취소/확정
- 도메인 검증만
- 다른 서비스 호출 없음

```java
package com.wellmeet.domain.reservation.service;

import com.wellmeet.domain.reservation.api.dto.CreateReservationRequest;
import com.wellmeet.domain.reservation.api.dto.UpdateReservationRequest;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class DomainReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation createReservation(CreateReservationRequest request) {
        // 1. 중복 체크
        if (alreadyReserved(request.memberId(), request.restaurantId(), request.availableDateId())) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
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

    public Reservation updateReservation(
        Long reservationId,
        String memberId,
        UpdateReservationRequest request
    ) {
        Reservation reservation = getByIdAndMemberId(reservationId, memberId);

        // 엔티티 메소드로 업데이트 (검증 포함)
        reservation.update(
            request.availableDateId(),
            request.partySize(),
            request.specialRequest()
        );

        return reservation;
    }

    public void cancelReservation(Long reservationId, String memberId) {
        Reservation reservation = getByIdAndMemberId(reservationId, memberId);
        reservation.cancel();
    }

    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_FOUND));
        reservation.confirm();
    }

    @Transactional(readOnly = true)
    public Reservation getByIdAndMemberId(Long id, String memberId) {
        return reservationRepository.findByIdAndMemberId(id, memberId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.NOT_FOUND_OR_UNAUTHORIZED));
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllByMemberId(String memberId) {
        return reservationRepository.findAllByMemberId(memberId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> findAllByRestaurantId(String restaurantId) {
        return reservationRepository.findAllByRestaurantId(restaurantId);
    }

    @Transactional(readOnly = true)
    public boolean alreadyReserved(String memberId, String restaurantId, Long availableDateId) {
        return reservationRepository.existsByMemberIdAndRestaurantIdAndAvailableDateId(
            memberId, restaurantId, availableDateId
        );
    }
}
```

---

### Step 3: DTO 정의

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/reservation/api/dto/`

#### CreateReservationRequest.java

```java
package com.wellmeet.domain.reservation.api.dto;

import jakarta.validation.constraints.*;

public record CreateReservationRequest(
    @NotBlank(message = "회원 ID는 필수입니다")
    String memberId,

    @NotBlank(message = "식당 ID는 필수입니다")
    String restaurantId,

    @NotNull(message = "예약 가능 날짜 ID는 필수입니다")
    Long availableDateId,

    @Min(value = 1, message = "최소 1명 이상이어야 합니다")
    int partySize,

    @Size(max = 255, message = "특별 요청사항은 255자 이하여야 합니다")
    String specialRequest
) {}
```

#### UpdateReservationRequest.java

```java
package com.wellmeet.domain.reservation.api.dto;

import jakarta.validation.constraints.*;

public record UpdateReservationRequest(
    @NotNull(message = "예약 가능 날짜 ID는 필수입니다")
    Long availableDateId,

    @Min(value = 1, message = "최소 1명 이상이어야 합니다")
    int partySize,

    @Size(max = 255, message = "특별 요청사항은 255자 이하여야 합니다")
    String specialRequest
) {}
```

#### ReservationResponse.java

```java
package com.wellmeet.domain.reservation.api.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    String memberId,
    String restaurantId,
    Long availableDateId,
    int partySize,
    String specialRequest,
    ReservationStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
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
            reservation.getCreatedAt(),
            reservation.getUpdatedAt()
        );
    }
}
```

**특징**:
- ✅ 단순 Reservation 필드만 포함
- ❌ Restaurant, Member 정보 없음 (BFF가 조합)

---

### Step 4: Application 설정

#### Application 클래스

**파일**: `domain-reservation/src/main/java/com/wellmeet/domain/ReservationServiceApplication.java`

⚠️ **전체 주석 처리** (Phase 1-3와 동일)

```java
//package com.wellmeet.domain;
//
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//@SpringBootApplication
//public class ReservationServiceApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(ReservationServiceApplication.class, args);
//    }
//}
```

#### application.yml

**파일**: `domain-reservation/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: domain-reservation-service
  datasource:
    url: jdbc:mysql://mysql-reservation:3306/wellmeet_reservation
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # Flyway가 스키마 관리
    properties:
      hibernate:
        format_sql: true
    show-sql: false
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration

server:
  port: 8085

eureka:
  client:
    service-url:
      defaultZone: http://discovery-server:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true

management:
  endpoints:
    web:
      exposure:
        include: health,info

# ❌ Redis 설정 없음 (domain-reservation은 Redis 사용 안 함)
```

---

### Step 5: build.gradle

**파일**: `domain-reservation/build.gradle`

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

**핵심**:
- ✅ infra-redis 없음
- ✅ 다른 domain 의존성 없음
- ✅ 순수 JPA + Flyway만

---

### Step 6: Dockerfile

**파일**: `domain-reservation/Dockerfile`

```dockerfile
# Stage 1: Build
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :domain-reservation:bootJar --no-daemon

# Stage 2: Runtime
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/domain-reservation/build/libs/*.jar app.jar

# Health Check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

### Step 7: docker-compose.yml

**수정 파일**: `docker-compose.yml` (루트)

```yaml
services:
  domain-reservation-service:
    build:
      context: .
      dockerfile: domain-reservation/Dockerfile
    container_name: domain-reservation-service
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
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8085/actuator/health"]
      interval: 30s
      timeout: 3s
      retries: 3
      start_period: 40s
```

**특징**:
- ✅ Redis 의존성 없음
- ✅ MySQL + Eureka만 의존

---

## BFF 역할

### api-user에서의 복잡한 로직 처리

**파일**: `api-user/src/main/java/com/wellmeet/reservation/ReservationService.java`

```java
package com.wellmeet.reservation;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.AvailableDateDomainService;
import com.wellmeet.domain.restaurant.entity.AvailableDate;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    // Phase 4: 직접 의존성
    private final ReservationDomainService reservationDomainService;
    private final AvailableDateDomainService availableDateDomainService;
    private final MemberDomainService memberDomainService;
    private final ReservationRedisService redisService;
    private final EventPublishService eventPublishService;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. BFF가 Redis 분산 락 획득
        if (!redisService.isReserving(memberId, request.restaurantId(), request.availableDateId())) {
            throw new AlreadyReservingException();
        }

        try {
            // 2. BFF가 Member 확인 (domain-member)
            Member member = memberDomainService.getById(memberId);

            // 3. BFF가 AvailableDate 조회 및 capacity 감소 (domain-restaurant)
            AvailableDate availableDate = availableDateDomainService.getById(request.availableDateId());
            availableDateDomainService.decreaseCapacity(availableDate.getId(), request.partySize());

            // 4. BFF가 Reservation 생성 (domain-reservation)
            Reservation reservation = reservationDomainService.createReservation(
                com.wellmeet.domain.reservation.api.dto.CreateReservationRequest.builder()
                    .memberId(memberId)
                    .restaurantId(request.restaurantId())
                    .availableDateId(request.availableDateId())
                    .partySize(request.partySize())
                    .specialRequest(request.specialRequest())
                    .build()
            );

            // 5. BFF가 이벤트 발행 (Kafka)
            eventPublishService.publish(ReservationCreatedEvent.from(reservation));

            // 6. BFF가 응답 조합
            return CreateReservationResponse.builder()
                    .id(reservation.getId())
                    .status(reservation.getStatus())
                    .restaurantName(availableDate.getRestaurant().getName())
                    .restaurantAddress(availableDate.getRestaurant().getAddress())
                    .date(availableDate.getDate())
                    .time(availableDate.getTime())
                    .partySize(reservation.getPartySize())
                    .memberName(member.getName())
                    .memberPhone(member.getPhoneNumber())
                    .build();

        } catch (Exception e) {
            // Phase 6에서 보상 트랜잭션 구현
            throw e;
        }
    }
}
```

**BFF 책임 요약**:
1. ✅ Redis 락 관리
2. ✅ 3개 domain 서비스 오케스트레이션
3. ✅ 트랜잭션 경계 관리
4. ✅ 이벤트 발행
5. ✅ 응답 데이터 조합

---

## 검증 계획

### Phase 4 완료 기준

**코드 구현** (100%):
- [x] DomainReservationController
- [x] DomainReservationService
- [x] DTO 클래스 (Request/Response)
- [x] application.yml (Redis 설정 없음)
- [x] build.gradle (infra-redis 의존성 없음)
- [x] Dockerfile
- [x] docker-compose.yml

**실행 검증** (0% - Application 클래스 활성화 필요):
- [ ] Application 클래스 주석 해제
- [ ] bootJar 빌드 성공
- [ ] Docker 컨테이너 실행 (포트 8085)
- [ ] Eureka 등록 확인
- [ ] Flyway 마이그레이션 성공
- [ ] REST API 응답 확인

**통합 테스트**:
- [ ] POST `/api/reservations` → Reservation 생성 확인
- [ ] GET `/api/reservations/member/{id}` → 조회 확인
- [ ] PATCH `/api/reservations/{id}/cancel` → 상태 변경 확인
- [ ] 중복 예약 체크 동작 확인

---

## 알려진 이슈

### 1. Application 클래스 빈 스캔 문제

**Phase 1-3 동일 이슈**:
- `@SpringBootApplication` 활성화 시 패키지 스캔 충돌
- 해결: `basePackages` 명시 또는 `@ComponentScan` 설정

### 2. Flyway 마이그레이션

**현재 상태**:
- `V1__init_schema.sql` 존재
- 9개 테이블 정의 (restaurant, member, owner 포함)

**주의사항**:
- `ddl-auto: validate` 사용 (Flyway가 스키마 관리)
- `baseline-on-migrate: true` (기존 DB 처리)

### 3. Phase 5 전환 준비

**변경 사항**:
- api-user: DomainService → Feign Client
- domain-reservation: 변경 없음 (단순 CRUD 유지)
- 트랜잭션 경계: BFF가 계속 관리

---

## Phase별 책임 비교

### Phase 4 (현재)
```
domain-reservation
└── Reservation CRUD + 도메인 검증

api-user (BFF)
├── Redis 락
├── domain-member 호출
├── domain-restaurant 호출 (capacity 관리)
├── domain-reservation 호출
└── 응답 조합
```

### Phase 5 (BFF 전환)
```
domain-reservation
└── 변경 없음

api-user (BFF)
├── Redis 락
├── MemberClient (Feign)
├── RestaurantClient (Feign)
├── ReservationClient (Feign)
└── 응답 조합
```

### Phase 6 (Saga Orchestrator)
```
domain-reservation
└── 변경 없음

ReservationOrchestrator (신규)
├── Redis 락
├── Saga 워크플로우
├── 보상 트랜잭션
└── 이벤트 발행

api-user (경량 BFF)
└── Orchestrator 호출
```

---

## 타임라인

### Week 1-2: 코드 구현
- Day 1-2: Controller + Service
- Day 3-4: DTO + 예외 처리
- Day 5-7: build.gradle, Dockerfile
- Day 8-10: 단위 테스트

### Week 2-3: 통합 및 검증
- Day 11-12: Application 활성화
- Day 13-14: bootJar 빌드
- Day 15-16: Docker 실행
- Day 17-18: Eureka + Flyway 검증

### Week 3-4: 최종 검증
- Day 19-21: E2E 테스트
- Day 22-23: BFF 통합 테스트
- Day 24-25: 문서 업데이트
- Day 26-28: Phase 4 완료 보고

---

## 다음 단계

**Phase 4 완료 후**:
1. ✅ 4개 domain 서비스 모두 독립 실행
2. ✅ Eureka 등록 확인
3. ✅ api-* 직접 의존성 유지 확인
4. ✅ Health check 통과

**Phase 5 준비**:
- Feign Client 인터페이스 설계
- Circuit Breaker 설정
- 성능 베이스라인 측정
- BFF 트랜잭션 전략 문서화

---

**작성자**: Claude AI Assistant
**최종 업데이트**: 2025-11-02
