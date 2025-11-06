# BFF 패턴 및 분산 트랜잭션 처리 전략

> WellMeet-Backend 프로젝트의 BFF(Backend for Frontend) 패턴과 분산 트랜잭션 처리 전략 가이드

## 📚 목차

1. [설계 원칙](#설계-원칙)
2. [Phase별 전략](#phase별-전략)
3. [예약 생성 플로우 예시](#예약-생성-플로우-예시)
4. [트랜잭션 처리 전략 비교](#트랜잭션-처리-전략-비교)
5. [권장 사항](#권장-사항)

---

## 설계 원칙

### Domain 서비스 책임

**제공하는 것** (✅):
- 자신의 도메인 엔티티 CRUD
- 도메인 검증 로직
- 단일 도메인 내 비즈니스 로직

**제공하지 않는 것** (❌):
- 다른 domain 서버 호출
- 분산 락 관리 (Redis 등)
- 데이터 조합 및 응답 생성
- 트랜잭션 오케스트레이션

### BFF(api-*) 책임

**제공하는 것** (✅):
- 여러 domain 서비스 오케스트레이션
- Redis 분산 락 관리
- 트랜잭션 경계 관리
- 응답 데이터 조합
- 이벤트 발행 (Kafka)
- 사용자 인증/권한 검증

---

## Phase별 전략

### Phase 4-5: BFF에서 모든 것 처리

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

### Phase 6: Saga Orchestrator 도입

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

---

## 예약 생성 플로우 예시

### Phase 4 (현재 - 직접 의존성)

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

### Phase 5 (Feign Client 전환)

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

### Phase 6 (Saga Orchestrator)

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

---

## 트랜잭션 처리 전략 비교

| 항목 | Phase 4 (직접 의존성) | Phase 5 (BFF) | Phase 6 (Saga) |
|------|---------------------|--------------|---------------|
| **트랜잭션 관리** | @Transactional | 수동 관리 | Saga Orchestrator |
| **일관성** | 강한 일관성 | 최종 일관성 | 최종 일관성 (보상) |
| **복잡도** | 낮음 | 중간 | 높음 |
| **확장성** | 낮음 | 중간 | 높음 |
| **장애 복구** | 롤백 | 수동 복구 | 자동 보상 |
| **네트워크 레이턴시** | 없음 | 있음 | 있음 |

---

## 권장 사항

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

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
