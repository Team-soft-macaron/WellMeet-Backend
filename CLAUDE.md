# WellMeet-Backend 테스트 구성 가이드

> 이 문서는 WellMeet-Backend 프로젝트의 테스트 작성 및 구성 표준을 정의합니다.

## 📚 목차

1. [프로젝트 구조](#프로젝트-구조)
2. [테스트 레이어별 구성](#테스트-레이어별-구성)
3. [모듈별 테스트 전략](#모듈별-테스트-전략)
4. [테스트 작성 규칙](#테스트-작성-규칙)
5. [테스트 인프라](#테스트-인프라)

---

## 프로젝트 구조

### 모듈 개요

```
WellMeet-Backend/
├── api-user/              # 사용자 API (REST Controller + Service)
├── api-owner/             # 사업자 API (REST Controller + Service)
├── domain-reservation/    # 예약 도메인 (Entity + Repository + Flyway)
├── domain-member/         # 회원 도메인 (Entity + Repository + testFixtures)
├── domain-owner/          # 사업자 도메인 (Entity + Repository + testFixtures)
├── domain-restaurant/     # 식당 도메인 (Entity + Repository + testFixtures)
├── infra-redis/           # Redis 분산 락 (Redisson 3.50.0)
├── infra-kafka/           # Kafka Producer (AWS MSK + IAM Auth)
├── batch-reminder/        # 예약 리마인더 배치 (Spring Batch)
└── discovery-server/      # Service Discovery (Eureka Server, Spring Cloud 2025.0.0)
```

### 의존성 관계

**현재 구조 (Phase 1: Monolithic)**:

```
api-user       → domain-reservation, domain-member, domain-owner, domain-restaurant, infra-redis, infra-kafka
api-owner      → domain-reservation, domain-member, domain-owner, domain-restaurant, infra-redis, infra-kafka
batch-reminder → domain-reservation, domain-member, domain-owner, domain-restaurant, infra-kafka

domain-reservation → (독립, Flyway 사용)
domain-member      → (독립)
domain-owner       → (독립)
domain-restaurant  → (독립)
infra-redis        → (독립)
infra-kafka        → (독립)
```

⚠️ **아키텍처 전환 계획**: 각 domain-* 모듈을 독립 서비스로 분리 예정 (Microservices 구조)
→ API 모듈에서 도메인 의존성 제거 후 REST API로 통신
→ infra-kafka를 통한 이벤트 기반 비동기 통신

---

## 🏗️ 아키텍처 마이그레이션 로드맵

### Phase 1: Monolithic 구조 (현재) → Phase 1.5 진행 중

**Phase 1.5 완료 사항** (2025-10-30):
- ✅ Service Discovery 인프라 구축 (Eureka Server)
- ✅ Docker Compose 환경 구성 (MySQL x4, Redis, Kafka, Eureka)
- ✅ Microservices 전환을 위한 기반 인프라 완료

### Phase 1: Monolithic 구조 (기본)

**특징**:

- API 모듈이 domain 모듈에 직접 의존
- 단일 애플리케이션으로 배포
- 빠른 개발 및 테스트 가능
- 모듈 간 직접 메소드 호출

**장점**:

- 간단한 배포 및 운영
- 트랜잭션 관리 용이
- 디버깅 및 추적이 쉬움
- 낮은 네트워크 오버헤드

**단점**:

- 서비스 간 결합도 높음
- 독립적인 스케일링 불가
- 한 모듈의 장애가 전체 시스템에 영향

### Phase 2: Microservices 구조 (목표)

**구조**:

```
api-user (Gateway)     →  [HTTP/REST]  →  domain-member (Service)
                       →  [HTTP/REST]  →  domain-restaurant (Service)
                       →  [HTTP/REST]  →  domain-reservation (Service)
                       →  [Kafka]      ↔  infra-kafka (Message Broker)

api-owner (Gateway)    →  [HTTP/REST]  →  domain-owner (Service)
                       →  [HTTP/REST]  →  domain-restaurant (Service)
                       →  [HTTP/REST]  →  domain-reservation (Service)
                       →  [Kafka]      ↔  infra-kafka (Message Broker)

batch-reminder         →  [HTTP/REST]  →  domain-reservation (Service)
                       →  [Kafka]      →  infra-kafka (Notification)
```

**특징**:

- 각 domain을 독립 서비스로 분리
- REST API로 서비스 간 통신
- Kafka를 통한 비동기 이벤트 기반 통신
- 각 서비스 독립 배포 및 스케일링

**장점**:

- 서비스 별 독립 배포 가능
- 기술 스택 다양화 가능
- 장애 격리 (Fault Isolation)
- 탄력적 스케일링

**단점**:

- 분산 트랜잭션 관리 복잡
- 네트워크 레이턴시 증가
- 운영 복잡도 상승 (모니터링, 로깅, 추적)

### 마이그레이션 전략

#### Step 1: API 인터페이스 추가 (Phase 1-2 코드 완성)

**Phase 1.5 완료 상태** (2025-10-30):
- ✅ Service Discovery 인프라 구축 (Eureka Server)
- ✅ Docker Compose 환경 구성 완료

**Phase 1-2 코드 완성 상태** (2025-10-31):
- ✅ domain-restaurant REST Controller 추가 완료 (DomainRestaurantController, 포트 8083)
- ✅ domain-member REST Controller 추가 완료 (MemberController, FavoriteRestaurantController, 포트 8082)
- ⏳ domain-owner REST Controller 추가 대기 중
- ⏳ domain-reservation REST Controller 추가 대기 중

**알려진 이슈**:
- ⚠️ domain-restaurant, domain-member 모두 Application 클래스 주석 처리로 독립 실행 검증 보류
- ⚠️ bootJar 빌드 및 Docker 컨테이너 실행 미검증

**다음 단계 (Phase 3)**: domain-owner 모듈에 REST Controller 추가

**목적**: 기존 의존성을 유지하면서 REST API 엔드포인트 동시 제공

#### Step 2: API 모듈에 HTTP Client 추가

- Spring Cloud OpenFeign 또는 RestTemplate 도입
- 각 domain 서비스를 호출하는 Client 인터페이스 생성
- Fallback 메커니즘 구현 (Circuit Breaker)

#### Step 3: 점진적 전환

1. **읽기 전용 API부터 전환**:
    - 조회(GET) 요청을 HTTP 호출로 전환
    - 기존 직접 호출과 병행 운영 (Feature Toggle)
2. **쓰기 API 전환**:
    - 생성/수정/삭제(POST/PUT/DELETE) 요청 전환
    - 트랜잭션 경계 재정의 (Saga Pattern 고려)
3. **의존성 제거**:
    - API 모듈의 domain 모듈 의존성 완전 제거
    - 독립 배포 가능 확인

#### Step 4: 서비스 분리 및 배포

- 각 domain 모듈을 독립 애플리케이션으로 전환
- Kubernetes 또는 ECS에 개별 서비스 배포
- Service Mesh 도입 고려 (Istio, Linkerd)

### 도메인 서비스 포트 할당

| 서비스 | 포트 | 현재 상태 |
|--------|------|---------|
| discovery-server | 8761 | ✅ 실행 중 |
| domain-restaurant-service | 8083 | ⚠️ 코드 완성 (실행 검증 보류) |
| domain-member-service | 8082 | ⚠️ 코드 완성 (실행 검증 보류) |
| domain-owner-service | 8084 | ⏳ 미구현 (Phase 3 예정) |
| domain-reservation-service | 8085 | ⏳ 미구현 (Phase 4 예정) |
| api-user | 8086 | 기존 Monolithic 실행 중 |
| api-owner | 8087 | 기존 Monolithic 실행 중 |

### 테스트 전략 변화

| 항목                   | Phase 1 (현재)       | Phase 2 (목표)                  |
|----------------------|--------------------|-------------------------------|
| **단위 테스트**           | 모듈 내 직접 호출         | Mock HTTP Client              |
| **통합 테스트**           | 실제 DB + Repository | Mock External Services        |
| **E2E 테스트**          | 단일 애플리케이션          | 멀티 서비스 환경 (Testcontainers)    |
| **Contract Testing** | 불필요                | Pact 또는 Spring Cloud Contract |
| **성능 테스트**           | JMeter (단일 앱)      | Gatling (분산 환경)               |

**Phase 2 테스트 가이드라인**:

- API 모듈: Mock 기반 단위 테스트 (WireMock 활용)
- Domain 서비스: 기존 통합 테스트 유지
- Contract Test: API 스펙 변경 시 자동 검증
- E2E Test: Docker Compose로 전체 서비스 환경 구성

### 고려사항

**분산 트랜잭션**:

- Saga Pattern 적용 (Choreography 또는 Orchestration)
- 보상 트랜잭션 (Compensating Transaction) 설계
- 이벤트 소싱 (Event Sourcing) 도입 검토

**데이터 일관성**:

- Eventual Consistency 허용 범위 정의
- CQRS (Command Query Responsibility Segregation) 패턴 고려
- 중복 데이터 관리 전략 (각 서비스가 필요한 데이터 복제)

**통신 방식**:

- 동기 통신: REST API (읽기, 즉시 응답 필요)
- 비동기 통신: Kafka 이벤트 (쓰기, 시간 지연 허용)

**모니터링 및 추적**:

- 분산 추적 (Distributed Tracing): Jaeger, Zipkin
- 중앙 로깅: ELK Stack, CloudWatch Logs Insights
- 메트릭 수집: Prometheus + Grafana

---

## BFF 패턴 및 분산 트랜잭션 처리 전략

### 설계 원칙

#### Domain 서비스 책임

**제공하는 것** (✅):
- 자신의 도메인 엔티티 CRUD
- 도메인 검증 로직
- 단일 도메인 내 비즈니스 로직

**제공하지 않는 것** (❌):
- 다른 domain 서버 호출
- 분산 락 관리 (Redis 등)
- 데이터 조합 및 응답 생성
- 트랜잭션 오케스트레이션

#### BFF(api-*) 책임

**제공하는 것** (✅):
- 여러 domain 서비스 오케스트레이션
- Redis 분산 락 관리
- 트랜잭션 경계 관리
- 응답 데이터 조합
- 이벤트 발행 (Kafka)
- 사용자 인증/권한 검증

### Phase별 전략

#### Phase 4-5: BFF에서 모든 것 처리

```
api-user (BFF)
├── Redis 분산 락 획득/해제
├── domain-member 호출 (직접 의존성 → Feign)
├── domain-restaurant 호출 (capacity 관리)
├── domain-reservation 호출 (예약 생성)
├── 응답 조합 (여러 domain 데이터 통합)
└── 이벤트 발행 (Kafka)
```

**특징**:
- 간단하고 안정적
- 트랜잭션 경계 명확
- 모든 비즈니스 로직이 BFF에 집중

#### Phase 6: Saga Orchestrator 도입

```
ReservationOrchestrator (신규 서비스)
├── 분산 트랜잭션 관리
├── 보상 트랜잭션 처리
├── Redis 분산 락 관리
└── 이벤트 발행

api-user (경량 BFF)
├── Orchestrator 호출
├── 응답 변환
└── 사용자 인증
```

**특징**:
- BFF 경량화
- 복잡한 트랜잭션 로직 분리
- 확장성 높음

### 예약 생성 플로우 예시

#### Phase 4 (현재 - 직접 의존성)

```java
// api-user/ReservationService.java
@Service
@Transactional
@RequiredArgsConstructor
public class ReservationService {

    // 직접 의존성
    private final ReservationDomainService reservationDomainService;
    private final RestaurantDomainService restaurantDomainService;
    private final MemberDomainService memberDomainService;
    private final ReservationRedisService redisService;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. BFF가 Redis 락 획득
        if (!redisService.isReserving(memberId, request.restaurantId(), request.availableDateId())) {
            throw new AlreadyReservingException();
        }

        // 2. BFF가 여러 domain 호출
        Member member = memberDomainService.getById(memberId);  // domain-member
        restaurantDomainService.decreaseCapacity(...);           // domain-restaurant
        Reservation reservation = reservationDomainService.create(...);  // domain-reservation

        // 3. BFF가 응답 조합
        return CreateReservationResponse.builder()
                .id(reservation.getId())
                .restaurantName(...)
                .memberName(member.getName())
                .build();
    }
}
```

**특징**:
- ✅ 단일 @Transactional로 일관성 보장
- ✅ 간단한 구조
- ❌ BFF가 무거워짐

#### Phase 5 (Feign Client 전환)

```java
// api-user/ReservationService.java
@Service
@RequiredArgsConstructor
public class ReservationService {

    // Feign Client 의존성
    private final MemberClient memberClient;
    private final RestaurantClient restaurantClient;
    private final ReservationClient reservationClient;
    private final ReservationRedisService redisService;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. Redis 락
        redisService.isReserving(...);

        // 2. Feign Client 호출
        MemberDTO member = memberClient.getMember(memberId);
        restaurantClient.decreaseCapacity(...);
        ReservationDTO reservation = reservationClient.create(...);

        // 3. 응답 조합
        return buildResponse(reservation, member, ...);
    }
}
```

**특징**:
- ✅ 완전한 BFF 패턴
- ✅ 독립 배포 가능
- ❌ 분산 트랜잭션 일관성 문제

#### Phase 6 (Saga Orchestrator)

```java
// api-user/ReservationService.java (경량화)
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationOrchestrator orchestrator;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // Orchestrator에 위임
        return orchestrator.executeReservationSaga(memberId, request);
    }
}

// reservation-orchestrator/ReservationSagaOrchestrator.java
@Service
public class ReservationSagaOrchestrator {

    private final MemberClient memberClient;
    private final RestaurantClient restaurantClient;
    private final ReservationClient reservationClient;

    public CreateReservationResponse executeReservationSaga(
        String memberId,
        CreateReservationRequest request
    ) {
        SagaTransaction saga = new SagaTransaction();

        try {
            // Step 1: Member 확인
            MemberDTO member = memberClient.getMember(memberId);

            // Step 2: Capacity 감소 + 보상 트랜잭션 등록
            saga.addStep(
                () -> restaurantClient.decreaseCapacity(...),
                () -> restaurantClient.increaseCapacity(...)  // 보상
            );

            // Step 3: Reservation 생성 + 보상
            saga.addStep(
                () -> reservationClient.create(...),
                () -> reservationClient.delete(...)  // 보상
            );

            return saga.execute();

        } catch (Exception e) {
            saga.compensate();  // 모든 보상 트랜잭션 실행
            throw e;
        }
    }
}
```

**특징**:
- ✅ 보상 트랜잭션 자동 처리
- ✅ BFF 경량화
- ✅ 확장성 높음

### 트랜잭션 처리 전략 비교

| 항목 | Phase 4 (직접 의존성) | Phase 5 (BFF) | Phase 6 (Saga) |
|------|---------------------|--------------|---------------|
| **트랜잭션 관리** | @Transactional | 수동 관리 | Saga Orchestrator |
| **일관성** | 강한 일관성 | 최종 일관성 | 최종 일관성 (보상) |
| **복잡도** | 낮음 | 중간 | 높음 |
| **확장성** | 낮음 | 중간 | 높음 |
| **장애 복구** | 롤백 | 수동 복구 | 자동 보상 |
| **네트워크 레이턴시** | 없음 | 있음 | 있음 |

### 권장 사항

**Phase 4-5 (BFF 전환까지)**:
- BFF에서 Redis 락 관리
- BFF에서 트랜잭션 오케스트레이션
- domain 서비스는 단순 CRUD만 제공
- 복잡한 비즈니스 로직은 BFF에 집중

**Phase 6 이후 (Saga 도입)**:
- Orchestrator로 트랜잭션 로직 이동
- BFF는 경량화 (인증, 응답 변환만)
- 보상 트랜잭션 자동화
- 이벤트 기반 아키텍처 강화

---

## 로컬 개발 환경

### Docker Compose 구성

WellMeet-Backend 프로젝트는 `docker-compose.yml`을 통해 로컬 개발에 필요한 모든 인프라를 제공합니다.

#### 인프라 컴포넌트

**데이터베이스 (MySQL 8.0)**:
- `mysql-reservation` - 예약 도메인 DB (포트: 3306)
- `mysql-member` - 회원 도메인 DB (포트: 3307)
- `mysql-owner` - 사업자 도메인 DB (포트: 3308)
- `mysql-restaurant` - 식당 도메인 DB (포트: 3309)

**메시징 및 캐시**:
- `redis` - 분산 락 및 캐시 (포트: 6379)
- `kafka` - 메시지 브로커 (포트: 9092)
- `zookeeper` - Kafka 코디네이터 (포트: 2181)

**서비스 디스커버리**:
- `discovery-server` - Eureka Server (포트: 8761)

#### 실행 방법

```bash
# 전체 인프라 시작
docker-compose up -d

# 특정 서비스만 시작
docker-compose up -d mysql-reservation redis

# 로그 확인
docker-compose logs -f discovery-server

# 전체 중지 및 제거
docker-compose down

# 볼륨까지 완전 삭제
docker-compose down -v
```

#### Phase 2 준비 사항

각 domain 모듈이 독립 서비스로 전환될 때를 대비하여:
- 각 도메인별로 별도의 MySQL 인스턴스 준비 완료
- Database per Service 패턴 적용 가능
- 서비스 간 데이터 격리 보장

---

### Service Discovery (Eureka Server)

#### 개요

Netflix Eureka를 기반으로 한 Service Registry로, Microservices 환경에서 서비스 인스턴스를 자동으로 등록하고 검색할 수 있게 합니다.

#### 기술 스택

- **Spring Boot**: 3.5.3
- **Spring Cloud**: 2025.0.0 (Northfields)
- **Eureka Server**: Netflix OSS

#### 주요 설정

**포트**: 8761

**Eureka 설정**:
```yaml
eureka:
  client:
    register-with-eureka: false  # Eureka Server 자체는 레지스트리에 등록하지 않음
    fetch-registry: false        # 단일 서버 구성, 다른 Eureka 서버로부터 레지스트리 가져오지 않음
  server:
    enable-self-preservation: false  # 개발 환경: 응답 없는 서비스 즉시 제거 (90초)
```

**Self Preservation 모드**:
- 프로덕션: `true` (네트워크 장애 시 서비스 정보 유지)
- 개발: `false` (빠른 피드백을 위해 비활성화)

#### 접속 정보

- **Dashboard**: http://localhost:8761
- **Health Check**: http://localhost:8761/actuator/health
- **Eureka Apps API**: http://localhost:8761/eureka/apps

#### Phase 2에서의 역할

각 domain 서비스가 Eureka Client로 등록되면:
1. 서비스 시작 시 자동으로 Eureka에 등록
2. 다른 서비스가 이름(service-id)으로 검색 가능
3. 헬스 체크를 통한 서비스 상태 모니터링
4. 로드 밸런싱 및 장애 복구 지원

#### Docker Compose 통합

discovery-server는 docker-compose.yml에 포함되어 있으며, Multi-stage Dockerfile로 빌드됩니다:

```dockerfile
# Stage 1: Gradle 빌드
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :discovery-server:bootJar --no-daemon

# Stage 2: 실행 환경
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/discovery-server/build/libs/*.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Health Check**:
- 간격: 30초
- 타임아웃: 3초
- 시작 대기: 40초

---

## 테스트 레이어별 구성

### 1. Entity Layer (domain-* 모듈)

**목적**: 도메인 객체의 생성, 검증, 비즈니스 규칙 테스트

**적용 모듈**:

- `domain-reservation` (예약)
- `domain-member` (회원)
- `domain-owner` (사업자)
- `domain-restaurant` (식당)

**위치**: `domain-{모듈명}/src/test/java/com/wellmeet/domain/{aggregate}/entity/`

**베이스 클래스**: 없음 (순수 단위 테스트)

**구성 예시**:

```java
package com.wellmeet.domain.restaurant.entity;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RestaurantTest {

    @Nested
    class ValidatePosition {

        @ParameterizedTest
        @ValueSource(doubles = {Restaurant.MINIMUM_LATITUDE - 0.1, Restaurant.MAXIMUM_LATITUDE + 0.1})
        void 위도는_일정_범위_이내여야_한다(double latitude) {
            assertThatThrownBy(() -> new Restaurant(
                    "id",
                    "name",
                    "address",
                    latitude,
                    127.0,
                    "thumbnail",
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LATITUDE.getMessage());
        }

        @ParameterizedTest
        @ValueSource(doubles = {Restaurant.MINIMUM_LONGITUDE - 0.1, Restaurant.MAXIMUM_LONGITUDE + 0.1})
        void 경도는_일정_범위_이내여야_한다(double longitude) {
            assertThatThrownBy(() -> new Restaurant(
                    "id",
                    "name",
                    "address",
                    37.5,
                    longitude,
                    "thumbnail",
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LONGITUDE.getMessage());
        }
    }

    @Nested
    class UpdateMetadata {

        @Test
        void 식당_이름을_변경할_수_있다() {
            Restaurant restaurant = createDefaultRestaurant();
            String newName = "변경된 식당명";

            restaurant.updateName(newName);

            assertThat(restaurant.getName()).isEqualTo(newName);
        }

        @Test
        void 식당_주소를_변경할_수_있다() {
            Restaurant restaurant = createDefaultRestaurant();
            String newAddress = "서울시 강남구 신사동";

            restaurant.updateAddress(newAddress);

            assertThat(restaurant.getAddress()).isEqualTo(newAddress);
        }
    }

    private Restaurant createDefaultRestaurant() {
        return new Restaurant(
                "id",
                "기본 식당",
                "서울시",
                37.5,
                127.0,
                "thumbnail",
                null
        );
    }
}
```

**작성 규칙**:

- ✅ `@Nested` 클래스로 테스트 메소드별 그룹화
- ✅ 테스트 메소드명은 한글로 작성 (언더스코어 사용)
- ✅ 정상 케이스 + 예외 케이스 모두 작성
- ✅ ParameterizedTest 활용 (반복 케이스)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지
- ❌ DB 접근 금지 (순수 객체 테스트)
- ❌ Mock 사용 금지

---

### 2. Repository Layer (domain-* 모듈)

**목적**: @Query 어노테이션으로 직접 작성한 커스텀 쿼리 메소드 테스트

**적용 모듈**:

- `domain-reservation` (예약)
- `domain-member` (회원)
- `domain-owner` (사업자)
- `domain-restaurant` (식당)

**위치**: `domain-{모듈명}/src/test/java/com/wellmeet/domain/{aggregate}/repository/`

**베이스 클래스**: `BaseRepositoryTest`

**테스트 대상**:

- ✅ @Query로 직접 작성한 JPQL/Native SQL 메소드
- ✅ 복잡한 조인, 집계 쿼리
- ✅ Custom Repository 구현체
- ❌ findById, save, findAll 등 자동 생성 메소드는 테스트하지 않음

**구성 예시**:

```java
package com.wellmeet.domain.restaurant.repository;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.model.BoundingBox;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestaurantRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Nested
    class FindWithBoundBox {

        @Test
        void BoundingBox_내의_식당만_조회한다() {
            Restaurant restaurant1 = createAndSaveRestaurant("식당1", 37.5, 127.0);
            Restaurant restaurant2 = createAndSaveRestaurant("식당2", 37.501, 127.001);
            Restaurant restaurant3 = createAndSaveRestaurant("식당3", 38.0, 128.0);

            BoundingBox boundingBox = new BoundingBox(37.4, 37.6, 126.9, 127.1);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result)
                    .hasSize(2)
                    .extracting(Restaurant::getName)
                    .containsExactlyInAnyOrder("식당1", "식당2");
        }

        @Test
        void BoundingBox_밖의_식당은_조회되지_않는다() {
            Restaurant restaurant = createAndSaveRestaurant("먼_식당", 38.0, 128.0);

            BoundingBox boundingBox = new BoundingBox(37.4, 37.6, 126.9, 127.1);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).isEmpty();
        }
    }

    private Restaurant createAndSaveRestaurant(String name, double lat, double lon) {
        Restaurant restaurant = new Restaurant(
                name,
                "description",
                "address",
                lat,
                lon,
                "thumbnail",
                null
        );
        return restaurantRepository.save(restaurant);
    }
}
```

**BaseRepositoryTest 구조**:

```java

@Import({JpaAuditingConfig.class})
@ExtendWith(DataBaseCleaner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseRepositoryTest {
}
```

**작성 규칙**:

- ✅ `BaseRepositoryTest` 상속 필수
- ✅ `@Nested` 클래스로 메소드별 그룹화
- ✅ 실제 DB(Testcontainers MySQL) 사용
- ✅ `@DataJpaTest`로 최소한의 컨텍스트만 로드
- ✅ **@Query로 직접 작성한 메소드만 테스트**
- ❌ findById, save, findAll 등 자동 생성 메소드는 테스트 작성하지 않음
- ❌ 비즈니스 로직 테스트 금지 (Domain Service에서)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 3. Domain Service Layer (domain-reservation 모듈)

**목적**: 도메인 비즈니스 로직 + Repository 통합 테스트

**위치**: `domain-reservation/src/test/java/com/wellmeet/domain/{aggregate}/`

**베이스 클래스**: `BaseRepositoryTest` (Repository 포함 테스트)

**구성 예시**:

```java
package com.wellmeet.domain.restaurant;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(RestaurantDomainService.class)
class RestaurantDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private RestaurantDomainService restaurantDomainService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Nested
    class FindNearbyRestaurants {

        @Test
        void BoundingBox를_계산하여_주변_식당을_조회한다() {
            createAndSaveRestaurant("식당1", 37.5, 127.0);
            createAndSaveRestaurant("식당2", 37.501, 127.001);
            createAndSaveRestaurant("먼식당", 38.0, 128.0);

            double userLat = 37.5;
            double userLon = 127.0;
            double radiusKm = 1.0;

            List<Restaurant> result = restaurantDomainService
                    .findNearbyRestaurants(userLat, userLon, radiusKm);

            assertThat(result)
                    .hasSize(2)
                    .extracting(Restaurant::getName)
                    .containsExactlyInAnyOrder("식당1", "식당2");
        }

        @Test
        void 반경_내에_식당이_없으면_빈_리스트를_반환한다() {
            createAndSaveRestaurant("먼식당", 38.0, 128.0);

            double userLat = 37.5;
            double userLon = 127.0;
            double radiusKm = 0.1;

            List<Restaurant> result = restaurantDomainService
                    .findNearbyRestaurants(userLat, userLon, radiusKm);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class UpdateRestaurantMetadata {

        @Test
        void 식당_메타데이터를_업데이트한다() {
            Restaurant restaurant = createAndSaveRestaurant("원본 식당", 37.5, 127.0);
            String newName = "수정된 식당";
            String newAddress = "서울시 강남구 신사동";

            Restaurant updated = restaurantDomainService
                    .updateRestaurantMetadata(restaurant.getId(), newName, newAddress);

            assertThat(updated.getName()).isEqualTo(newName);
            assertThat(updated.getAddress()).isEqualTo(newAddress);

            Restaurant persisted = restaurantRepository.findById(restaurant.getId())
                    .orElseThrow();
            assertThat(persisted.getName()).isEqualTo(newName);
        }

        @Test
        void 존재하지_않는_식당_조회_시_예외가_발생한다() {
            String nonExistentId = "non-existent-id";

            assertThatThrownBy(() ->
                    restaurantDomainService.getRestaurantById(nonExistentId))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessageContaining("존재하지 않는 식당");
        }
    }

    private Restaurant createAndSaveRestaurant(String name, double lat, double lon) {
        Restaurant restaurant = new Restaurant(
                name,
                "description",
                "address",
                lat,
                lon,
                "thumbnail",
                null
        );
        return restaurantRepository.save(restaurant);
    }
}
```

**작성 규칙**:

- ✅ `@Import(DomainService.class)` 명시
- ✅ `@Nested` 클래스로 메소드별 그룹화
- ✅ Repository와 함께 통합 테스트
- ✅ 비즈니스 로직 검증 (계산, 변환, 유효성)
- ✅ 예외 상황 처리 검증
- ✅ 트랜잭션 롤백 확인
- ❌ Controller 로직 포함 금지
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 4. Service Layer (api-user, api-owner 모듈)

**목적**: Application Service 비즈니스 로직 + Mock 기반 단위 테스트 또는 통합 테스트

**위치**: `api-{user|owner}/src/test/java/com/wellmeet/{feature}/`

**베이스 클래스**: Mock 사용 시 없음, 통합 테스트 시 `BaseServiceTest`

**구성 예시 - 단위 테스트 (Mock)**:

```java
package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationDomainService reservationDomainService;

    @InjectMocks
    private ReservationService reservationService;

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            Restaurant restaurant = createRestaurant("Test Restaurant");
            AvailableDate availableDate = createAvailableDate(LocalDateTime.now(), 10, restaurant);
            Member member1 = createMember("Test");
            Member member2 = createMember("Test2");
            Reservation reservation1 = createReservation(restaurant, availableDate, member1, 4);
            Reservation reservation2 = createReservation(restaurant, availableDate, member2, 2);
            List<Reservation> reservations = List.of(reservation1, reservation2);

            when(reservationDomainService.findAllByRestaurantId(restaurant.getId()))
                    .thenReturn(reservations);

            List<ReservationResponse> expectedReservations = reservationService.getReservations(restaurant.getId());

            assertThat(expectedReservations).hasSize(reservations.size());
        }
    }

    private Restaurant createRestaurant(String name) {
        return new Restaurant(name, "description", "address", 32.1, 37.1, "thumbnail", new Owner("name", "email"));
    }

    private AvailableDate createAvailableDate(LocalDateTime dateTime, int capacity, Restaurant restaurant) {
        return new AvailableDate(dateTime.toLocalDate(), dateTime.toLocalTime(), capacity, restaurant);
    }

    private Member createMember(String name) {
        return new Member(name, "nickname", "email@email.com", "phone");
    }

    private Reservation createReservation(Restaurant restaurant, AvailableDate availableDate, Member member,
                                          int partySize) {
        return new Reservation(restaurant, availableDate, member, partySize, "request");
    }
}
```

**구성 예시 - 통합 테스트 (BaseServiceTest)**:

```java
package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.wellmeet.BaseServiceTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRedisService reservationRedisService;

    @BeforeEach
    void setUp() {
        reservationRedisService.deleteReservationLock();
    }

    @Nested
    class Reserve {

        @Test
        void 한_사람이_같은_예약_요청을_동시에_여러번_신청해도_한_번만_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1);
            int capacity = 100;
            AvailableDate availableDate = availableDateGenerator.generate(
                    LocalDateTime.now().plusDays(1), capacity, restaurant1
            );
            int partySize = 4;
            CreateReservationRequest request = new CreateReservationRequest(
                    restaurant1.getId(), availableDate.getId(), partySize, "request"
            );
            Member member = memberGenerator.generate("test");

            runAtSameTime(500, () -> reservationService.reserve(member.getId(), request));

            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate = availableDateRepository.findById(availableDate.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(1),
                    () -> assertThat(foundAvailableDate.getMaxCapacity()).isEqualTo(capacity - partySize)
            );
        }

        @Test
        void 여러_사람이_예약_요청을_동시에_신청해도_적절히_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1);
            int capacity = 100;
            AvailableDate availableDate = availableDateGenerator.generate(
                    LocalDateTime.now().plusDays(1), capacity, restaurant1
            );
            int partySize = 2;
            CreateReservationRequest request = new CreateReservationRequest(
                    restaurant1.getId(), availableDate.getId(), partySize, "request"
            );
            List<Runnable> tasks = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                Member member = memberGenerator.generate("member" + i);
                tasks.add(() -> reservationService.reserve(member.getId(), request));
            }

            runAtSameTime(tasks);

            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate = availableDateRepository.findById(availableDate.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(50),
                    () -> assertThat(foundAvailableDate.getMaxCapacity()).isZero()
            );
        }
    }
}
```

**작성 규칙**:

- ✅ `@Nested` 클래스로 메소드별 그룹화
- ✅ 단위 테스트: Mock 사용, 빠른 실행
- ✅ 통합 테스트: `BaseServiceTest` 상속, 실제 DB
- ✅ 동시성 테스트: `runAtSameTime()` 유틸 활용
- ✅ DTO 변환 로직 검증
- ❌ HTTP 요청/응답 테스트 금지 (Controller에서)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 5. Controller Layer (api-user, api-owner 모듈)

**목적**: REST API E2E 테스트 (HTTP → Service → DB)

**위치**: `api-{user|owner}/src/test/java/com/wellmeet/{feature}/`

**베이스 클래스**: `BaseControllerTest`

**구성 예시**:

```java
package com.wellmeet.favorite;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FavoriteControllerTest extends BaseControllerTest {

    @Nested
    class GetFavoriteRestaurants {

        @Test
        void 즐겨찾기_레스토랑_조회() {
            Member testUser = memberGenerator.generate("test");
            Member anotherUser = memberGenerator.generate("another");
            Owner owner1 = ownerGenerator.generate("Owner1");
            Owner owner2 = ownerGenerator.generate("Owner2");
            Owner owner3 = ownerGenerator.generate("Owner3");
            Restaurant restaurant1 = restaurantGenerator.generate("Restaurant 1", owner1);
            Restaurant restaurant2 = restaurantGenerator.generate("Restaurant 2", owner2);
            Restaurant restaurant3 = restaurantGenerator.generate("Restaurant 3", owner3);
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser, restaurant1));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser, restaurant2));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(anotherUser, restaurant2));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(anotherUser, restaurant3));

            FavoriteRestaurantResponse[] responses = given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().get("/user/favorite/restaurant/list")
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(FavoriteRestaurantResponse[].class);

            assertThat(responses).hasSize(2);
            assertThat(responses[0].getId()).isEqualTo(restaurant1.getId());
            assertThat(responses[1].getId()).isEqualTo(restaurant2.getId());
        }
    }

    @Nested
    class AddFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_추가() {
            Member testUser = memberGenerator.generate("testUser");
            Owner owner = ownerGenerator.generate("Test Owner");
            Restaurant restaurant = restaurantGenerator.generate("Test Restaurant", owner);

            FavoriteRestaurantResponse response = given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().post("/user/favorite/restaurant/{restaurantId}", restaurant.getId())
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(FavoriteRestaurantResponse.class);

            assertThat(response.getId()).isEqualTo(restaurant.getId());
        }
    }

    @Nested
    class RemoveFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_삭제() {
            Member testUser = memberGenerator.generate("testUser");
            Owner owner = ownerGenerator.generate("Test Owner");
            Restaurant restaurant = restaurantGenerator.generate("Test Restaurant", owner);
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser, restaurant));

            given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().delete("/user/favorite/restaurant/{restaurantId}", restaurant.getId())
                    .then().statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
```

**BaseControllerTest 구조**:

```java

@ExtendWith(DataBaseCleaner.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }
}
```

**작성 규칙**:

- ✅ `BaseControllerTest` 상속 필수
- ✅ `@Nested` 클래스로 API별 그룹화
- ✅ REST Assured 사용
- ✅ HTTP 상태 코드 검증
- ✅ 응답 본문 구조 검증
- ✅ 성공/실패 케이스 모두 작성
- ✅ 인증/권한 검증 (헤더)
- ❌ Mock 사용 금지 (E2E는 실제 흐름)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 6. Redis Service Layer (infra-redis 모듈)

**목적**: 분산 락, 캐싱 로직 테스트

**위치**: `infra-redis/src/test/java/com/wellmeet/{feature}/`

**베이스 클래스**: Testcontainers 기반 통합 테스트

**주요 기술**: Redisson 3.50.0 (분산 락 라이브러리)

**구성 예시**:

```java
package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.reservation.ReservationRedisService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ReservationRedisServiceTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    }

    @Autowired
    private ReservationRedisService reservationRedisService;

    @Nested
    class IsReserving {

        @Test
        void 동시_요청_시_하나만_락을_획득한다() throws InterruptedException {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            int threadCount = 10;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() -> {
                    try {
                        boolean acquired = reservationRedisService
                                .isReserving(memberId, restaurantId, availableDateId);
                        if (acquired) {
                            successCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executorService.shutdown();

            assertThat(successCount.get()).isEqualTo(1);
        }
    }

    @Nested
    class IsUpdating {

        @Test
        void 락을_정상적으로_획득하고_해제한다() {
            String memberId = "member-1";
            Long reservationId = 1L;

            boolean acquired = reservationRedisService.isUpdating(memberId, reservationId);

            assertThat(acquired).isTrue();

            boolean retry = reservationRedisService.isUpdating(memberId, reservationId);
            assertThat(retry).isFalse();
        }
    }

    @Nested
    class DeleteReservationLock {

        @Test
        void 락_삭제_후_다시_획득_가능하다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            Long availableDateId = 1L;

            reservationRedisService.isReserving(memberId, restaurantId, availableDateId);

            reservationRedisService.deleteReservationLock();

            boolean reacquired = reservationRedisService
                    .isReserving(memberId, restaurantId, availableDateId);
            assertThat(reacquired).isTrue();
        }
    }
}
```

**작성 규칙**:

- ✅ Testcontainers로 실제 Redis 사용
- ✅ `@Nested` 클래스로 메소드별 그룹화
- ✅ 동시성 테스트 필수
- ✅ 락 획득/해제 사이클 검증
- ✅ 타임아웃 시나리오 테스트
- ❌ Mock Redis 사용 금지 (분산 락은 실제 환경 필수)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 7. Kafka Producer Layer (infra-kafka 모듈)

**목적**: 메시지 발송, 직렬화, 에러 처리 테스트

**위치**: `infra-kafka/src/test/java/com/wellmeet/kafka/`

**베이스 클래스**: EmbeddedKafka 기반 통합 테스트

**주요 기술**: AWS MSK (Managed Streaming for Apache Kafka) + IAM 인증

⚠️ **현재 상태**: 테스트 미작성 (아래 예시는 향후 작성을 위한 가이드)

**구성 예시**:

```java
package com.wellmeet.kafka.service;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.kafka.dto.NotificationMessage;
import com.wellmeet.kafka.dto.payload.ReservationCreatedPayload;
import java.time.LocalDateTime;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@EmbeddedKafka(
        partitions = 1,
        topics = {"notification"},
        brokerProperties = {
                "listeners=PLAINTEXT://localhost:9092",
                "port=9092"
        }
)
@DirtiesContext
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private BlockingQueue<NotificationMessage> receivedMessages = new LinkedBlockingQueue<>();

    @KafkaListener(topics = "notification", groupId = "test-group")
    public void listen(NotificationMessage message) {
        receivedMessages.add(message);
    }

    @Nested
    class SendNotificationMessage {

        @Test
        void 예약_생성_알림_메시지를_발송한다() throws InterruptedException {
            ReservationCreatedPayload payload = ReservationCreatedPayload.builder()
                    .reservationId("reservation-1")
                    .restaurantName("맛집")
                    .reservationDate(LocalDateTime.now().plusDays(1))
                    .partySize(4)
                    .build();

            String memberId = "member-1";

            kafkaProducerService.sendNotificationMessage(memberId, payload);

            NotificationMessage received = receivedMessages.poll(5, TimeUnit.SECONDS);
            assertThat(received).isNotNull();
            assertThat(received.getHeader().getRecipientId()).isEqualTo(memberId);
            assertThat(received.getPayload()).isInstanceOf(ReservationCreatedPayload.class);

            ReservationCreatedPayload receivedPayload =
                    (ReservationCreatedPayload) received.getPayload();
            assertThat(receivedPayload.getReservationId()).isEqualTo("reservation-1");
            assertThat(receivedPayload.getRestaurantName()).isEqualTo("맛집");
        }

        @Test
        void 직렬화_역직렬화가_정상적으로_동작한다() throws InterruptedException {
            ReservationCreatedPayload payload = ReservationCreatedPayload.builder()
                    .reservationId("res-123")
                    .restaurantName("한식당")
                    .reservationDate(LocalDateTime.of(2025, 12, 25, 18, 0))
                    .partySize(2)
                    .build();

            kafkaProducerService.sendNotificationMessage("member-1", payload);

            NotificationMessage received = receivedMessages.poll(5, TimeUnit.SECONDS);
            assertThat(received).isNotNull();

            ReservationCreatedPayload receivedPayload =
                    (ReservationCreatedPayload) received.getPayload();
            assertThat(receivedPayload.getReservationDate())
                    .isEqualTo(LocalDateTime.of(2025, 12, 25, 18, 0));
        }
    }
}
```

**작성 규칙**:

- ✅ `@EmbeddedKafka` 사용
- ✅ `@Nested` 클래스로 메소드별 그룹화
- ✅ Consumer로 메시지 수신 검증
- ✅ 직렬화/역직렬화 검증
- ✅ 타임아웃 설정 (5초)
- ✅ `@DirtiesContext`로 컨텍스트 격리
- ❌ 실제 Kafka 브로커 연결 금지 (테스트 환경)
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

### 8. Batch Job Layer (batch-reminder 모듈)

**목적**: Spring Batch Job 실행 및 검증

**위치**: `batch-reminder/src/test/java/com/wellmeet/batch/`

**베이스 클래스**: `TestBatchConfig` 포함

⚠️ **현재 상태**: 테스트 미작성 (아래 예시는 향후 작성을 위한 가이드)

**구성 예시**:

```java
package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.batch.config.TestBatchConfig;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestBatchConfig.class)
class ReservationReminderJobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Nested
    class ExecuteReminderJob {

        @Test
        void 한_시간_전_예약_리마인더_배치가_성공한다() throws Exception {
            Member member = createMember();
            Restaurant restaurant = createRestaurant();
            Reservation reservation = createReservation(
                    member,
                    restaurant,
                    LocalDateTime.now().plusHours(1)
            );

            JobExecution jobExecution = jobLauncherTestUtils.launchJob();

            assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            assertThat(jobExecution.getStepExecutions()).hasSize(1);
        }
    }
}
```

**작성 규칙**:

- ✅ `JobLauncherTestUtils` 사용
- ✅ `@Nested` 클래스로 Job별 그룹화
- ✅ Job 실행 상태 검증
- ✅ Step 실행 결과 검증
- ✅ Reader/Processor/Writer 개별 테스트
- ✅ Clock 주입으로 시간 제어
- ❌ given, when, then 주석 사용 금지
- ❌ @DisplayName 사용 금지

---

## 모듈별 테스트 전략

### domain-reservation 모듈

| Layer          | 테스트 타입 | 베이스 클래스                      | 주요 검증                |
|----------------|--------|------------------------------|----------------------|
| Entity         | 단위 테스트 | 없음                           | 생성, 검증, 비즈니스 규칙      |
| Repository     | 통합 테스트 | BaseRepositoryTest           | @Query 커스텀 쿼리만       |
| Domain Service | 통합 테스트 | BaseRepositoryTest + @Import | 비즈니스 로직 + Repository |

**특징**: Flyway를 통한 DB 마이그레이션 관리
**커버리지 목표**: 85%

---

### domain-member 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증              |
|------------|--------|--------------------|--------------------|
| Entity     | 단위 테스트 | 없음                 | 회원 생성, 검증, 비즈니스 규칙 |
| Repository | 통합 테스트 | BaseRepositoryTest | @Query 커스텀 쿼리만     |

**특징**: testFixtures 제공 (다른 모듈에서 재사용 가능)
**커버리지 목표**: 85%

---

### domain-owner 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증               |
|------------|--------|--------------------|---------------------|
| Entity     | 단위 테스트 | 없음                 | 사업자 생성, 검증, 비즈니스 규칙 |
| Repository | 통합 테스트 | BaseRepositoryTest | @Query 커스텀 쿼리만      |

**특징**: testFixtures 제공 (다른 모듈에서 재사용 가능)
**커버리지 목표**: 85%

---

### domain-restaurant 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증                    |
|------------|--------|--------------------|--------------------------|
| Entity     | 단위 테스트 | 없음                 | 식당 생성, 좌표 검증, 메타데이터 관리   |
| Repository | 통합 테스트 | BaseRepositoryTest | BoundingBox 쿼리, 위치 기반 조회 |

**특징**:

- testFixtures 제공 (다른 모듈에서 재사용 가능)
- 좌표 기반 쿼리 (BoundingBox, 거리 계산)
  **커버리지 목표**: 85%

---

### api-user / api-owner 모듈

| Layer          | 테스트 타입 | 베이스 클래스                 | 주요 검증          |
|----------------|--------|-------------------------|----------------|
| Controller     | E2E    | BaseControllerTest      | HTTP API 전체 흐름 |
| Service        | 단위/통합  | Mock 또는 BaseServiceTest | 비즈니스 로직, 동시성   |
| Event Listener | 통합 테스트 | BaseServiceTest         | 이벤트 발행/수신      |

**커버리지 목표**: 80%

---

### infra-redis 모듈

| Layer         | 테스트 타입 | 베이스 클래스        | 주요 검증     |
|---------------|--------|----------------|-----------|
| Redis Service | 통합 테스트 | Testcontainers | 분산 락, 동시성 |

**특징**: Redisson 3.50.0 사용 (분산 락 라이브러리)
**커버리지 목표**: 90% (Critical - 동시성 제어 핵심 모듈)

---

### infra-kafka 모듈

| Layer    | 테스트 타입 | 베이스 클래스       | 주요 검증       |
|----------|--------|---------------|-------------|
| Producer | 통합 테스트 | EmbeddedKafka | 메시지 발송, 직렬화 |
| DTO      | 단위 테스트 | 없음            | 직렬화/역직렬화    |

**특징**: AWS MSK + IAM 인증 사용
⚠️ **현재 상태**: 테스트 미작성
**커버리지 목표**: 70% (작성 후)

---

### batch-reminder 모듈

| Layer      | 테스트 타입 | 베이스 클래스         | 주요 검증         |
|------------|--------|-----------------|---------------|
| Job Config | 통합 테스트 | TestBatchConfig | Job 실행 성공     |
| Processor  | 단위 테스트 | 없음              | 데이터 변환 로직     |
| Writer     | 단위/통합  | Mock/실제         | 외부 호출 (Kafka) |

⚠️ **현재 상태**: 테스트 미작성
**커버리지 목표**: 75% (작성 후)

---

## 테스트 작성 규칙

### 1. 네이밍 컨벤션

```java
// ✅ Good - @Nested + 한글 메소드명
class RestaurantTest {

    @Nested
    class ValidatePosition {

        @Test
        void 위도는_일정_범위_이내여야_한다() {
        }

        @Test
        void 경도는_일정_범위_이내여야_한다() {
        }
    }

    @Nested
    class UpdateMetadata {

        @Test
        void 식당_이름을_변경할_수_있다() {
        }
    }
}

// ❌ Bad - @DisplayName 사용
@DisplayName("Restaurant 엔티티")
class RestaurantTest {

    @Test
    @DisplayName("위도 검증")
    void validateLatitude() {
    }
}
```

---

### 2. 주석 없이 코드로 표현

```java
// ✅ Good - 주석 없이 바로 코드
@Test
void 예약을_생성한다() {
    Member member = createMember();
    Restaurant restaurant = createRestaurant();
    CreateReservationRequest request = new CreateReservationRequest(...);

    CreateReservationResponse response = reservationService.create(member.getId(), request);

    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
}

// ❌ Bad - given, when, then 주석 사용
@Test
void createReservation() {
    // given
    Member member = createMember();

    // when
    Reservation reservation = service.create(member);

    // then
    assertThat(reservation).isNotNull();
}
```

---

### 3. AssertJ 사용

```java
// ✅ Good - AssertJ
assertThat(result).

isNotNull();

assertThat(result.getName()).

isEqualTo("식당");

assertThat(list).

hasSize(3).

extracting(Restaurant::getName).

containsExactly("A","B","C");

// ❌ Bad - JUnit Assertions
assertTrue(result !=null);

assertEquals("식당",result.getName());
```

---

### 4. 예외 테스트

```java
// ✅ Good
@Test
void 잘못된_입력_시_예외가_발생한다() {
    assertThatThrownBy(() -> service.doSomething())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("잘못된 입력");
}
```

---

### 5. ParameterizedTest 활용

```java

@ParameterizedTest
@ValueSource(ints = {0, -1, -100})
void 인원_수가_0_이하면_예외가_발생한다(int partySize) {
    assertThatThrownBy(() -> Reservation.create(partySize))
            .isInstanceOf(IllegalArgumentException.class);
}

@ParameterizedTest
@CsvSource({
        "37.5, 127.0, 1.0, 2",
        "37.5, 127.0, 5.0, 5",
        "37.5, 127.0, 10.0, 10"
})
void 반경_내_식당을_조회한다(double lat, double lon, double radius, int expectedCount) {
    List<Restaurant> result = service.findNearby(lat, lon, radius);
    assertThat(result).hasSize(expectedCount);
}
```

---

## 테스트 인프라

### 1. Gradle 설정

**루트 build.gradle**:

```gradle
subprojects {
    apply plugin: 'jacoco'

    jacoco {
        toolVersion = "0.8.11"
    }

    test {
        useJUnitPlatform()
        finalizedBy jacocoTestReport
    }

    jacocoTestReport {
        dependsOn test
        reports {
            xml.required = true
            html.required = true
        }
    }

    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = 0.70
                }
            }
        }
    }
}
```

**모듈별 build.gradle (domain-redis 예시)**:

```gradle
dependencies {
    testImplementation 'org.testcontainers:testcontainers:1.19.3'
    testImplementation 'org.testcontainers:junit-jupiter:1.19.3'
}
```

**kafka 모듈 build.gradle**:

```gradle
dependencies {
    testImplementation 'org.springframework.kafka:spring-kafka-test'
}
```

---

### 2. 테스트 설정 (application-test.yml)

**domain-reservation 모듈** (`domain-reservation/src/main/resources/application-domain-test.yml`):

```yaml
spring:
  config:
    activate:
      on-profile: domain-test
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/test
    username: root
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    properties:
      hibernate:
        format_sql: true
    show-sql: false
```

**infra-redis 모듈** (`infra-redis/src/main/resources/application-infra-redis-test.yml`):

```yaml
spring:
  config:
    activate:
      on-profile: infra-redis-test
  data:
    redis:
      host: localhost
      port: 6379
```

**infra-kafka 모듈** (`infra-kafka/src/main/resources/application-infra-kafka-test.yml`):

```yaml
spring:
  config:
    activate:
      on-profile: infra-kafka-test
  kafka:
    bootstrap-servers: ${spring.embedded.kafka.brokers}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

**api-user/api-owner 모듈** (`api-user/src/main/resources/application-test.yml`):

```yaml
spring:
  config:
    import:
      - application-domain-test.yml
      - application-infra-redis-test.yml
      - application-infra-kafka-test.yml
```

---

### 3. Test Fixtures (Gradle testFixtures 플러그인)

WellMeet-Backend 프로젝트는 Gradle의 `java-test-fixtures` 플러그인을 사용하여 테스트 데이터 생성 코드를 모듈 간 재사용할 수 있도록 구성합니다.

#### testFixtures 적용 모듈

- `domain-reservation` → 예약, 예약 가능 날짜 생성
- `domain-member` → 회원, 즐겨찾기 생성
- `domain-owner` → 사업자 생성
- `domain-restaurant` → 식당, 메뉴 생성

#### build.gradle 설정

**domain 모듈 (예: domain-member/build.gradle)**:

```gradle
plugins {
    id 'java-library'
    id 'java-test-fixtures'  // testFixtures 플러그인 활성화
}

dependencies {
    // 일반 의존성
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

    // testFixtures에서 필요한 의존성
    testFixturesImplementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    testFixturesImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

**API 모듈 (예: api-user/build.gradle)**:

```gradle
dependencies {
    // domain 모듈 의존성
    implementation project(':domain-member')
    implementation project(':domain-owner')
    implementation project(':domain-restaurant')
    implementation project(':domain-reservation')

    // testFixtures 사용
    testImplementation(testFixtures(project(':domain-member')))
    testImplementation(testFixtures(project(':domain-owner')))
    testImplementation(testFixtures(project(':domain-restaurant')))
    testImplementation(testFixtures(project(':domain-reservation')))
}
```

#### 디렉토리 구조

```
domain-member/
├── src/
│   ├── main/java/               # 프로덕션 코드
│   ├── test/java/               # 모듈 내부 테스트
│   └── testFixtures/java/       # 다른 모듈에서 사용 가능한 Test Fixture
│       └── com/wellmeet/domain/member/
│           ├── MemberFixture.java
│           └── FavoriteRestaurantFixture.java
```

#### Generator 패턴 구현 예시

**domain-restaurant/src/testFixtures/java/com/wellmeet/domain/restaurant/RestaurantFixture.java**:

```java
package com.wellmeet.domain.restaurant;

import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantFixture {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant create(String name, Owner owner) {
        return create(name, 37.5, 127.0, owner);
    }

    public Restaurant create(String name, double lat, double lon, Owner owner) {
        Restaurant restaurant = Restaurant.builder()
                .name(name)
                .address("서울시 강남구")
                .latitude(lat)
                .longitude(lon)
                .phoneNumber("02-1234-5678")
                .owner(owner)
                .thumbnailUrl("https://example.com/thumbnail.jpg")
                .build();
        return restaurantRepository.save(restaurant);
    }
}
```

**domain-member/src/testFixtures/java/com/wellmeet/domain/member/MemberFixture.java**:

```java
package com.wellmeet.domain.member;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberFixture {

    @Autowired
    private MemberRepository memberRepository;

    public Member create(String name) {
        return create(name, name + "@example.com");
    }

    public Member create(String name, String email) {
        Member member = Member.builder()
                .name(name)
                .nickname(name + "_nick")
                .email(email)
                .phoneNumber("010-1234-5678")
                .build();
        return memberRepository.save(member);
    }
}
```

#### API 모듈에서 사용 예시

**api-user/src/test/java/com/wellmeet/reservation/ReservationServiceTest.java**:

```java

@SpringBootTest
class ReservationServiceTest {

    @Autowired
    private MemberFixture memberFixture;  // domain-member testFixtures

    @Autowired
    private OwnerFixture ownerFixture;  // domain-owner testFixtures

    @Autowired
    private RestaurantFixture restaurantFixture;  // domain-restaurant testFixtures

    @Autowired
    private ReservationService reservationService;

    @Test
    void 예약을_생성한다() {
        // testFixtures를 활용한 데이터 준비
        Member member = memberFixture.create("testUser");
        Owner owner = ownerFixture.create("testOwner");
        Restaurant restaurant = restaurantFixture.create("테스트 식당", owner);

        // 비즈니스 로직 테스트
        ReservationResponse response = reservationService.reserve(...);

        assertThat(response).isNotNull();
    }
}
```

#### testFixtures의 장점

1. **재사용성**: 여러 모듈에서 동일한 테스트 데이터 생성 로직 공유
2. **일관성**: 도메인 객체 생성 방식이 중앙화되어 일관성 유지
3. **유지보수**: 도메인 모델 변경 시 testFixtures만 수정하면 됨
4. **캡슐화**: 도메인 지식을 testFixtures에 캡슐화
5. **독립성**: 각 도메인 모듈이 자신의 testFixtures 제공

#### 주의사항

- testFixtures는 **테스트 전용**이며, 프로덕션 코드에서 사용 불가
- testFixtures 간 의존성은 최소화 (순환 의존성 방지)
- Repository를 주입받아 실제 DB에 저장하는 방식 사용
- 복잡한 비즈니스 로직은 testFixtures에 포함하지 않음

---

## 인프라 통합

### Flyway 데이터베이스 마이그레이션

#### 개요

`domain-reservation` 모듈에서만 Flyway를 사용하여 데이터베이스 스키마 버전 관리를 수행합니다.

#### 적용 위치

- **모듈**: `domain-reservation`
- **마이그레이션 파일**: `domain-reservation/src/main/resources/db/migration/`
- **실행 시점**: Spring Boot 애플리케이션 시작 시 자동 실행

#### build.gradle 설정

```gradle
dependencies {
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'
}
```

#### application.yml 설정

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-suffix: .sql
```

#### 마이그레이션 파일 네이밍

```
db/migration/
├── V1__create_reservation_table.sql
├── V2__create_available_date_table.sql
├── V3__add_status_column_to_reservation.sql
└── V4__add_index_on_reservation_date.sql
```

**규칙**:

- `V{버전번호}__{설명}.sql` 형식
- 버전 번호는 순차적으로 증가
- 실행 순서는 버전 번호 기준

#### 다른 domain 모듈

다른 domain 모듈(member, owner, restaurant)은 Flyway를 사용하지 않고 JPA `ddl-auto` 설정 사용:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop  # 테스트 환경
```

**이유**:

- `domain-reservation`은 예약 데이터의 히스토리 관리가 중요하여 스키마 변경 추적 필요
- 다른 모듈은 상대적으로 단순한 CRUD 작업 위주

#### 테스트 환경에서의 Flyway

테스트 환경에서도 Flyway가 자동 실행되어 일관된 스키마 환경 보장:

**domain-reservation/src/test/resources/application-domain-test.yml**:

```yaml
spring:
  flyway:
    enabled: true
    clean-on-validation-error: true  # 테스트 시 스키마 초기화
```

---

### AWS MSK (Kafka) 통합

#### 개요

`infra-kafka` 모듈은 AWS MSK (Managed Streaming for Apache Kafka)와 IAM 인증을 사용하여 메시지 브로커 통합을 제공합니다.

#### build.gradle 설정

```gradle
dependencies {
    implementation 'org.springframework.kafka:spring-kafka'
    implementation 'software.amazon.msk:aws-msk-iam-auth:2.2.0'
    implementation 'com.amazonaws:aws-java-sdk-kafka:1.12.565'
}
```

#### 특징

**IAM 인증 사용**:

- AWS IAM Role 기반 인증
- Access Key/Secret Key 불필요
- ECS/EKS에서 Task Role 또는 Pod Identity 활용

**보안**:

- TLS 암호화 통신
- VPC 내부 통신
- Security Group 기반 접근 제어

#### application.yml 설정 (프로덕션)

```yaml
spring:
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS}
    security:
      protocol: SASL_SSL
    properties:
      sasl.mechanism: AWS_MSK_IAM
      sasl.jaas.config: software.amazon.msk.auth.iam.IAMLoginModule required;
      sasl.client.callback.handler.class: software.amazon.msk.auth.iam.IAMClientCallbackHandler
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      acks: all
      retries: 3
```

#### 테스트 환경

테스트 환경에서는 EmbeddedKafka 사용 (IAM 인증 불필요):

**application-infra-kafka-test.yml**:

```yaml
spring:
  kafka:
    bootstrap-servers: ${spring.embedded.kafka.brokers}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

#### 주요 토픽

- `notification` - 사용자 알림 메시지
- `reservation-created` - 예약 생성 이벤트
- `reservation-cancelled` - 예약 취소 이벤트
- `reminder` - 리마인더 메시지 (batch-reminder 모듈에서 사용)

#### 메시지 구조 예시

```json
{
  "header": {
    "messageId": "msg-123",
    "recipientId": "member-456",
    "timestamp": "2025-10-30T12:00:00Z",
    "type": "RESERVATION_CREATED"
  },
  "payload": {
    "reservationId": "reservation-789",
    "restaurantName": "맛집",
    "reservationDate": "2025-11-01T19:00:00",
    "partySize": 4
  }
}
```

#### Producer 예시

**infra-kafka/src/main/java/com/wellmeet/kafka/service/KafkaProducerService.java**:

```java

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public void sendNotificationMessage(String memberId, Object payload) {
        NotificationMessage message = NotificationMessage.builder()
                .header(MessageHeader.builder()
                        .messageId(UUID.randomUUID().toString())
                        .recipientId(memberId)
                        .timestamp(LocalDateTime.now())
                        .build())
                .payload(payload)
                .build();

        kafkaTemplate.send("notification", memberId, message);
    }
}
```

#### 모니터링

**CloudWatch Metrics**:

- 메시지 발송 성공/실패율
- 지연 시간 (Latency)
- Consumer Lag

**Kafka 로그**:

- Producer 전송 로그
- 재시도 횟수
- 오류 메시지

---

## 테스트 커버리지 목표

**전체 프로젝트 목표**: 75% 이상

---

## 체크리스트

### 테스트 작성 전

- [ ] 어떤 레이어인지 확인 (Entity/Repository/Service/Controller)
- [ ] 적절한 베이스 클래스 선택
- [ ] 필요한 의존성 확인 (Testcontainers, EmbeddedKafka 등)
- [ ] 테스트 데이터 준비 방법 결정 (Fixture vs 직접 생성)
- [ ] Repository 테스트 시 @Query 메소드인지 확인

### 테스트 작성 중

- [ ] `@Nested` 클래스로 메소드별 그룹화
- [ ] 테스트 메소드명을 한글로 작성
- [ ] AssertJ로 검증
- [ ] 정상 케이스 + 예외 케이스 작성
- [ ] Edge case 고려 (null, 빈 문자열, 경계값)
- [ ] given, when, then 주석 사용하지 않음
- [ ] @DisplayName 사용하지 않음

### 테스트 작성 후

- [ ] 테스트 실행 성공 확인
- [ ] 커버리지 확인 (JaCoCo 리포트)
- [ ] 불필요한 @Disabled 제거
- [ ] 테스트 속도 확인 (느리면 Mock 고려)

---

## 참고 자료

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [AssertJ Documentation](https://assertj.github.io/doc/)
- [Testcontainers Documentation](https://www.testcontainers.org/)
- [Spring Boot Testing Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [REST Assured Guide](https://rest-assured.io/)

---

**마지막 업데이트**: 2025-10-30

## 변경 이력

**2025-10-30 (Phase 1 인프라 완료)**:

- discovery-server 모듈 추가 (Eureka Server, Spring Cloud 2025.0.0)
- docker-compose.yml 생성 (MySQL x4, Redis, Kafka, Eureka)
- settings.gradle 및 build.gradle 업데이트
- Service Discovery 인프라 구축 완료
- Microservices 전환을 위한 기반 인프라 완성
- 로컬 개발 환경 섹션 추가 (Docker Compose, Eureka Server)
- Phase 1.5 진행 상태 문서화

**2025-10-30 (초기)**:

- 프로젝트 구조 업데이트 (3개 신규 도메인 모듈 추가: member, owner, restaurant)
- 모듈명 변경 반영 (domain-redis → infra-redis, kafka → infra-kafka)
- Microservices 아키텍처 마이그레이션 로드맵 추가
- testFixtures 패턴 상세 문서화
- Flyway 및 AWS MSK 통합 문서화
- 테스트 설정 파일 경로 업데이트
- 테스트 미작성 모듈 명시 (infra-kafka, batch-reminder)

**2025-10-05**:

- 초기 문서 작성
