# 로컬 개발 환경 가이드

> WellMeet-Backend 프로젝트의 로컬 개발 환경 구성 (Docker Compose, Eureka Server)

## 📚 목차

1. [Docker Compose 구성](#docker-compose-구성)
2. [Service Discovery (Eureka Server)](#service-discovery-eureka-server)

---

## Docker Compose 구성

WellMeet-Backend 프로젝트는 `docker-compose.yml`을 통해 로컬 개발에 필요한 모든 인프라를 제공합니다.

### 인프라 컴포넌트

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

### 실행 방법

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

### Phase 2 준비 사항

각 domain 모듈이 독립 서비스로 전환될 때를 대비하여:
- 각 도메인별로 별도의 MySQL 인스턴스 준비 완료
- Database per Service 패턴 적용 가능
- 서비스 간 데이터 격리 보장

---

## Service Discovery (Eureka Server)

### 개요

Netflix Eureka를 기반으로 한 Service Registry로, Microservices 환경에서 서비스 인스턴스를 자동으로 등록하고 검색할 수 있게 합니다.

### 기술 스택

- **Spring Boot**: 3.5.3
- **Spring Cloud**: 2025.0.0 (Northfields)
- **Eureka Server**: Netflix OSS

### 주요 설정

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

### 접속 정보

- **Dashboard**: http://localhost:8761
- **Health Check**: http://localhost:8761/actuator/health
- **Eureka Apps API**: http://localhost:8761/eureka/apps

### Phase 2에서의 역할

각 domain 서비스가 Eureka Client로 등록되면:
1. 서비스 시작 시 자동으로 Eureka에 등록
2. 다른 서비스가 이름(service-id)으로 검색 가능
3. 헬스 체크를 통한 서비스 상태 모니터링
4. 로드 밸런싱 및 장애 복구 지원

### Docker Compose 통합

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

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
