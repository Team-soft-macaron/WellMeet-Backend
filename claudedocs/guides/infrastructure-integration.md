# 인프라 통합 가이드

> WellMeet-Backend 프로젝트의 인프라 통합 (Flyway, AWS MSK) 설정 가이드

## 📚 목차

1. [Flyway 데이터베이스 마이그레이션](#flyway-데이터베이스-마이그레이션)
2. [AWS MSK (Kafka) 통합](#aws-msk-kafka-통합)

---

## Flyway 데이터베이스 마이그레이션

### 개요

`domain-reservation` 모듈에서만 Flyway를 사용하여 데이터베이스 스키마 버전 관리를 수행합니다.

### 적용 위치

- **모듈**: `domain-reservation`
- **마이그레이션 파일**: `domain-reservation/src/main/resources/db/migration/`
- **실행 시점**: Spring Boot 애플리케이션 시작 시 자동 실행

### build.gradle 설정

```gradle
dependencies {
    implementation 'org.flywaydb:flyway-core'
    implementation 'org.flywaydb:flyway-mysql'
}
```

### application.yml 설정

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-suffix: .sql
```

### 마이그레이션 파일 네이밍

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

### 다른 domain 모듈

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

### 테스트 환경에서의 Flyway

테스트 환경에서도 Flyway가 자동 실행되어 일관된 스키마 환경 보장:

**domain-reservation/src/test/resources/application-domain-test.yml**:

```yaml
spring:
  flyway:
    enabled: true
    clean-on-validation-error: true  # 테스트 시 스키마 초기화
```

---

## AWS MSK (Kafka) 통합

### 개요

`infra-kafka` 모듈은 AWS MSK (Managed Streaming for Apache Kafka)와 IAM 인증을 사용하여 메시지 브로커 통합을 제공합니다.

### build.gradle 설정

```gradle
dependencies {
    implementation 'org.springframework.kafka:spring-kafka'
    implementation 'software.amazon.msk:aws-msk-iam-auth:2.2.0'
    implementation 'com.amazonaws:aws-java-sdk-kafka:1.12.565'
}
```

### 특징

**IAM 인증 사용**:

- AWS IAM Role 기반 인증
- Access Key/Secret Key 불필요
- ECS/EKS에서 Task Role 또는 Pod Identity 활용

**보안**:

- TLS 암호화 통신
- VPC 내부 통신
- Security Group 기반 접근 제어

### application.yml 설정 (프로덕션)

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

### 테스트 환경

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

### 주요 토픽

- `notification` - 사용자 알림 메시지
- `reservation-created` - 예약 생성 이벤트
- `reservation-cancelled` - 예약 취소 이벤트
- `reminder` - 리마인더 메시지 (batch-reminder 모듈에서 사용)

### 메시지 구조 예시

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

### Producer 예시

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

### 모니터링

**CloudWatch Metrics**:

- 메시지 발송 성공/실패율
- 지연 시간 (Latency)
- Consumer Lag

**Kafka 로그**:

- Producer 전송 로그
- 재시도 횟수
- 오류 메시지

---

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
