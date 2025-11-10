# WellMeet-Backend 테스트 구성 가이드

> 이 문서는 WellMeet-Backend 프로젝트의 테스트 작성 및 구성 표준을 정의합니다.

## 📚 목차

1. [프로젝트 구조](#프로젝트-구조)
2. [테스트 커버리지 목표](#테스트-커버리지-목표)
3. [체크리스트](#체크리스트)
4. [참고 자료](#참고-자료)

---

## 프로젝트 구조

### 모듈 개요

```
WellMeet-Backend/
├── common-client/              # 공통 Record DTOs (api-* 모듈 간 공유)
├── domain-common/              # 공통 도메인 유틸리티
├── api-user/                   # 사용자 BFF (Feign Clients + Redis + Kafka + Saga)
├── api-owner/                  # 사업자 BFF (Feign Clients + Redis + Kafka)
├── domain-reservation/         # 예약 도메인 서비스 (Entity + Repository + Flyway)
├── domain-member/              # 회원 도메인 서비스 (Entity + Repository + testFixtures)
├── domain-owner/               # 사업자 도메인 서비스 (Entity + Repository + testFixtures)
├── domain-restaurant/          # 식당 도메인 서비스 (Entity + Repository + testFixtures)
├── infra-redis/                # Redis 분산 락 (Redisson 3.50.0)
├── infra-kafka/                # Kafka Producer (AWS MSK + IAM Auth)
├── platform-saga/              # Saga 오케스트레이션 (분산 트랜잭션 관리)
├── batch-reminder/             # 예약 리마인더 배치 (Spring Batch)
└── platform-discovery-server/  # Service Discovery (Eureka Server, Spring Cloud 2025.0.0)
```

### 의존성 관계

```
api-user       → common-client, infra-redis, infra-kafka, platform-saga
api-owner      → common-client, infra-redis, infra-kafka
batch-reminder → domain-reservation, domain-member, domain-owner, domain-restaurant, infra-kafka

domain-* 모듈 → (독립, 상호 의존성 없음)
infra-* 모듈  → (독립)
platform-* 모듈 → (독립)
```

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
