# 모듈별 테스트 전략

> WellMeet-Backend 프로젝트의 모듈별 테스트 전략 및 커버리지 목표

## 📚 목차

1. [domain-reservation 모듈](#domain-reservation-모듈)
2. [domain-member 모듈](#domain-member-모듈)
3. [domain-owner 모듈](#domain-owner-모듈)
4. [domain-restaurant 모듈](#domain-restaurant-모듈)
5. [api-user / api-owner 모듈](#api-user--api-owner-모듈)
6. [infra-redis 모듈](#infra-redis-모듈)
7. [infra-kafka 모듈](#infra-kafka-모듈)
8. [batch-reminder 모듈](#batch-reminder-모듈)

---

## domain-reservation 모듈

| Layer          | 테스트 타입 | 베이스 클래스                      | 주요 검증                |
|----------------|--------|------------------------------|----------------------|
| Entity         | 단위 테스트 | 없음                           | 생성, 검증, 비즈니스 규칙      |
| Repository     | 통합 테스트 | BaseRepositoryTest           | @Query 커스텀 쿼리만       |
| Domain Service | 통합 테스트 | BaseRepositoryTest + @Import | 비즈니스 로직 + Repository |

**특징**: Flyway를 통한 DB 마이그레이션 관리
**커버리지 목표**: 85%

---

## domain-member 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증              |
|------------|--------|--------------------|--------------------|
| Entity     | 단위 테스트 | 없음                 | 회원 생성, 검증, 비즈니스 규칙 |
| Repository | 통합 테스트 | BaseRepositoryTest | @Query 커스텀 쿼리만     |

**특징**: testFixtures 제공 (다른 모듈에서 재사용 가능)
**커버리지 목표**: 85%

---

## domain-owner 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증               |
|------------|--------|--------------------|---------------------|
| Entity     | 단위 테스트 | 없음                 | 사업자 생성, 검증, 비즈니스 규칙 |
| Repository | 통합 테스트 | BaseRepositoryTest | @Query 커스텀 쿼리만      |

**특징**: testFixtures 제공 (다른 모듈에서 재사용 가능)
**커버리지 목표**: 85%

---

## domain-restaurant 모듈

| Layer      | 테스트 타입 | 베이스 클래스            | 주요 검증                    |
|------------|--------|--------------------|--------------------------|
| Entity     | 단위 테스트 | 없음                 | 식당 생성, 좌표 검증, 메타데이터 관리   |
| Repository | 통합 테스트 | BaseRepositoryTest | BoundingBox 쿼리, 위치 기반 조회 |

**특징**:

- testFixtures 제공 (다른 모듈에서 재사용 가능)
- 좌표 기반 쿼리 (BoundingBox, 거리 계산)
  **커버리지 목표**: 85%

---

## api-user / api-owner 모듈

| Layer          | 테스트 타입 | 베이스 클래스                 | 주요 검증          |
|----------------|--------|-------------------------|----------------|
| Controller     | E2E    | BaseControllerTest      | HTTP API 전체 흐름 |
| Service        | 단위/통합  | Mock 또는 BaseServiceTest | 비즈니스 로직, 동시성   |
| Event Listener | 통합 테스트 | BaseServiceTest         | 이벤트 발행/수신      |

**커버리지 목표**: 80%

---

## infra-redis 모듈

| Layer         | 테스트 타입 | 베이스 클래스        | 주요 검증     |
|---------------|--------|----------------|-----------|
| Redis Service | 통합 테스트 | Testcontainers | 분산 락, 동시성 |

**특징**: Redisson 3.50.0 사용 (분산 락 라이브러리)
**커버리지 목표**: 90% (Critical - 동시성 제어 핵심 모듈)

---

## infra-kafka 모듈

| Layer    | 테스트 타입 | 베이스 클래스       | 주요 검증       |
|----------|--------|---------------|-------------|
| Producer | 통합 테스트 | EmbeddedKafka | 메시지 발송, 직렬화 |
| DTO      | 단위 테스트 | 없음            | 직렬화/역직렬화    |

**특징**: AWS MSK + IAM 인증 사용
⚠️ **현재 상태**: 테스트 미작성
**커버리지 목표**: 70% (작성 후)

---

## batch-reminder 모듈

| Layer      | 테스트 타입 | 베이스 클래스         | 주요 검증         |
|------------|--------|-----------------|---------------|
| Job Config | 통합 테스트 | TestBatchConfig | Job 실행 성공     |
| Processor  | 단위 테스트 | 없음              | 데이터 변환 로직     |
| Writer     | 단위/통합  | Mock/실제         | 외부 호출 (Kafka) |

⚠️ **현재 상태**: 테스트 미작성
**커버리지 목표**: 75% (작성 후)

---

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
