# 클래스 네이밍 규칙 가이드

> **적용일**: 2025-11-05
> **목적**: 프로젝트 전체에서 클래스 이름의 유일성을 보장하고 일관된 네이밍 패턴 적용

## 📚 목차

1. [핵심 원칙](#핵심-원칙)
2. [Domain 모듈 네이밍 규칙](#domain-모듈-네이밍-규칙)
3. [BFF 모듈 네이밍 규칙](#bff-모듈-네이밍-규칙)
4. [테스트 클래스 네이밍 규칙](#테스트-클래스-네이밍-규칙)
5. [레이어별 접미사 정리](#레이어별-접미사-정리)
6. [마이그레이션 히스토리](#마이그레이션-히스토리)
7. [주의사항](#주의사항)

---

## 핵심 원칙

1. **계층명은 마지막에 위치**: `XxxController`, `XxxService` (❌ `ControllerXxx`, `ServiceXxx`)
2. **프로젝트 전체에서 클래스 이름 유일성 보장**: 단일 모듈이 아닌 전체 프로젝트 기준
3. **테스트 클래스도 동일 규칙 적용**: `{TargetClassName}Test`

---

## Domain 모듈 네이밍 규칙

### Domain Controllers (domain-* 모듈)

**패턴**: `{Entity}DomainController`

**예시**:
```
domain-restaurant/
├── RestaurantDomainController.java          (레스토랑 도메인 컨트롤러)
├── RestaurantAvailableDateController.java   (예약 가능 날짜 컨트롤러)
├── RestaurantBusinessHourController.java    (영업 시간 컨트롤러)
├── RestaurantMenuController.java            (메뉴 컨트롤러)
└── RestaurantReviewController.java          (리뷰 컨트롤러)

domain-member/
├── MemberDomainController.java              (회원 도메인 컨트롤러)
└── MemberFavoriteRestaurantController.java  (즐겨찾기 컨트롤러)

domain-owner/
└── OwnerDomainController.java               (사업자 도메인 컨트롤러)

domain-reservation/
└── ReservationDomainController.java         (예약 도메인 컨트롤러)
```

**네이밍 이유**:
- `DomainController` 접미사로 도메인 서비스의 REST API임을 명확히 표시
- 엔티티명을 접두사로 사용하여 도메인 구분
- BFF 모듈의 컨트롤러와 명확히 구분

---

### ApplicationService (domain-* 모듈)

**패턴**: `{Domain}{Entity}ApplicationService`

**예시**:
```
domain-restaurant/
├── RestaurantApplicationService.java                    (레스토랑 애플리케이션 서비스)
├── RestaurantAvailableDateApplicationService.java      (예약 가능 날짜)
├── RestaurantBusinessHourApplicationService.java       (영업 시간)
├── RestaurantMenuApplicationService.java               (메뉴)
└── RestaurantReviewApplicationService.java             (리뷰)

domain-member/
├── MemberApplicationService.java                        (회원)
└── MemberFavoriteRestaurantApplicationService.java     (즐겨찾기)

domain-owner/
└── OwnerApplicationService.java                         (사업자)

domain-reservation/
└── ReservationApplicationService.java                   (예약)
```

**네이밍 이유**:
- ApplicationService는 Controller와 DomainService 사이의 오케스트레이션 레이어
- Domain 접두사로 소속 도메인을 명확히 표시
- DomainService와 구분하여 레이어 역할 명확화

---

### DomainService (domain-* 모듈)

**패턴**: `{Entity}DomainService`

**예시**:
```
domain-restaurant/
├── RestaurantDomainService.java
├── AvailableDateDomainService.java
├── BusinessHourDomainService.java
├── MenuDomainService.java
└── ReviewDomainService.java

domain-member/
├── MemberDomainService.java
└── FavoriteRestaurantDomainService.java

domain-owner/
└── OwnerDomainService.java

domain-reservation/
└── ReservationDomainService.java
```

**네이밍 이유**:
- DomainService는 순수 비즈니스 로직을 담당
- ApplicationService와 명확히 구분

---

## BFF 모듈 네이밍 규칙

### BFF Controllers (api-user, api-owner 모듈)

**패턴**: `{User|Owner}{Feature}BffController`

**예시**:
```
api-user/
├── UserFavoriteRestaurantBffController.java   (사용자 즐겨찾기)
├── UserReservationBffController.java          (사용자 예약)
└── UserRestaurantBffController.java           (사용자 레스토랑)

api-owner/
├── OwnerReservationBffController.java         (사업자 예약)
└── OwnerRestaurantBffController.java          (사업자 레스토랑)
```

**네이밍 이유**:
- `Bff` 접두사로 Backend for Frontend 패턴임을 명확히 표시
- `User` 또는 `Owner` 접두사로 사용자 구분
- Domain 모듈의 Controller와 이름 충돌 방지

---

### BFF Services (api-user, api-owner 모듈)

**패턴**: `{User|Owner}{Feature}BffService`

**예시**:
```
api-user/
├── UserFavoriteRestaurantBffService.java
├── UserReservationBffService.java
├── UserRestaurantBffService.java
└── UserEventPublishBffService.java

api-owner/
├── OwnerReservationBffService.java
├── OwnerRestaurantBffService.java
└── OwnerEventPublishBffService.java
```

**네이밍 이유**:
- Controller와 동일한 네이밍 패턴 적용
- 여러 Domain 서비스를 오케스트레이션하는 역할 명확화

---

### Feign Clients (api-user, api-owner 모듈)

**패턴**: `{Domain}{Entity}FeignClient`

**예시**:
```
api-user, api-owner 공통:
├── MemberFeignClient.java                          (회원 도메인)
├── MemberFavoriteRestaurantFeignClient.java       (즐겨찾기)
├── OwnerFeignClient.java                           (사업자 도메인)
├── ReservationFeignClient.java                     (예약 도메인)
├── RestaurantFeignClient.java                      (레스토랑 도메인)
└── RestaurantAvailableDateFeignClient.java         (예약 가능 날짜)
```

**네이밍 이유**:
- `FeignClient` 접미사로 HTTP 통신 인터페이스임을 명확히 표시
- Domain 접두사로 호출 대상 도메인 명시
- 향후 Microservices 전환 시 변경 최소화

---

## 테스트 클래스 네이밍 규칙

**패턴**: `{TargetClassName}Test`

**예시**:
```
프로덕션 코드:
- RestaurantDomainController.java
- UserReservationBffController.java
- MemberFeignClient.java

테스트 코드:
- RestaurantDomainControllerTest.java
- UserReservationBffControllerTest.java
- MemberFeignClientTest.java
```

**네이밍 이유**:
- 테스트 대상 클래스를 명확히 식별
- 표준 Java 테스트 네이밍 컨벤션 준수

---

## 레이어별 접미사 정리

| 레이어 | 접미사 | 예시 | 위치 |
|--------|--------|------|------|
| Domain REST Controller | `DomainController` | `RestaurantDomainController` | domain-* |
| Domain Application Service | `ApplicationService` | `RestaurantApplicationService` | domain-* |
| Domain Business Service | `DomainService` | `RestaurantDomainService` | domain-* |
| BFF Controller | `BffController` | `UserReservationBffController` | api-* |
| BFF Service | `BffService` | `UserReservationBffService` | api-* |
| Feign Client | `FeignClient` | `ReservationFeignClient` | api-* |
| Test | `Test` | `RestaurantDomainControllerTest` | */test/** |

---

## 마이그레이션 히스토리

**적용일**: 2025-11-05
**변경 파일**: 총 49개 (프로덕션 38개 + 테스트 11개)

**Phase 1: domain-restaurant (10개)**
- Controllers: 5개
- ApplicationServices: 5개

**Phase 2: domain-member/owner/reservation (5개)**
- domain-member: 3개
- domain-owner: 1개
- domain-reservation: 1개

**Phase 3: api-user (13개 + 7개 테스트)**
- BFF Controllers/Services: 7개
- Feign Clients: 6개
- 테스트: 7개

**Phase 4: api-owner (9개 + 4개 테스트)**
- BFF Controllers/Services: 5개
- Feign Clients: 4개
- 테스트: 4개

**Phase 5: 검증 완료**
- ✅ 전체 빌드 성공
- ✅ 테스트 컴파일 성공

---

## 주의사항

1. **신규 클래스 생성 시**: 반드시 이 네이밍 규칙을 따라야 함
2. **충돌 확인**: 프로젝트 전체에서 클래스 이름 검색 후 생성
3. **테스트 클래스**: 프로덕션 코드와 동일한 패턴 적용
4. **import 문**: 패키지 경로로 구분되므로 동일 클래스명 사용 불가

---

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
