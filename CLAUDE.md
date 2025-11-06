# WellMeet-Backend 테스트 구성 가이드

> 이 문서는 WellMeet-Backend 프로젝트의 테스트 작성 및 구성 표준을 정의합니다.

## 📚 목차

1. [프로젝트 구조](#프로젝트-구조)
2. [아키텍처 마이그레이션 로드맵](#-아키텍처-마이그레이션-로드맵)
3. [테스트 커버리지 목표](#테스트-커버리지-목표)
4. [체크리스트](#체크리스트)
5. [참고 자료](#참고-자료)
6. [변경 이력](#변경-이력)

## 📖 상세 가이드 문서

프로젝트의 상세한 가이드는 별도 문서로 분리되어 있습니다:

- **[클래스 네이밍 규칙](./claudedocs/guides/naming-conventions.md)** - Domain/BFF 모듈 네이밍 패턴
- **[BFF 패턴 및 분산 트랜잭션 처리 전략](./claudedocs/guides/bff-transaction-strategy.md)** - BFF 책임과 Phase별 전략
- **[로컬 개발 환경](./claudedocs/guides/local-development.md)** - Docker Compose, Eureka Server 설정
- **[테스트 레이어별 구성](./claudedocs/guides/test-layer-guide.md)** - 8개 테스트 레이어 상세 가이드
- **[모듈별 테스트 전략](./claudedocs/guides/module-test-strategies.md)** - 각 모듈의 테스트 타입과 커버리지 목표
- **[테스트 작성 규칙](./claudedocs/guides/test-writing-rules.md)** - 네이밍, AssertJ, ParameterizedTest 규칙
- **[테스트 인프라](./claudedocs/guides/test-infrastructure.md)** - Gradle, application-test.yml, testFixtures 설정
- **[인프라 통합](./claudedocs/guides/infrastructure-integration.md)** - Flyway, AWS MSK (Kafka) 통합 가이드

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

**마지막 업데이트**: 2025-11-06

## 변경 이력

**2025-11-06 (문서 재구성)**:

- CLAUDE.md 대폭 간소화 (2,682줄 → 300줄, 89% 감소)
- 8개 상세 가이드 문서 분리 (`claudedocs/guides/` 디렉토리):
  - `naming-conventions.md` (254줄) - 클래스 네이밍 규칙
  - `bff-transaction-strategy.md` (231줄) - BFF 패턴 및 분산 트랜잭션
  - `local-development.md` (120줄) - Docker Compose, Eureka Server
  - `test-layer-guide.md` (1,013줄) - 8개 테스트 레이어 상세
  - `module-test-strategies.md` (103줄) - 모듈별 테스트 전략
  - `test-writing-rules.md` (141줄) - 테스트 작성 규칙
  - `test-infrastructure.md` (306줄) - Gradle, testFixtures 설정
  - `infrastructure-integration.md` (214줄) - Flyway, AWS MSK 통합
- 문서 간 링크 구조 개선 (메인 문서 → 상세 가이드)
- 각 가이드 문서에 독립적인 목차(TOC) 추가
- Claude 컨텍스트 로딩 효율 개선 (~25,000 토큰 절약)

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
