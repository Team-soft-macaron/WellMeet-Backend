# 테스트 인프라 가이드

> WellMeet-Backend 프로젝트의 테스트 인프라 구성 및 설정 가이드

## 📚 목차

1. [Gradle 설정](#1-gradle-설정)
2. [테스트 설정 (application-test.yml)](#2-테스트-설정-application-testyml)
3. [Test Fixtures (Gradle testFixtures 플러그인)](#3-test-fixtures-gradle-testfixtures-플러그인)

---

## 1. Gradle 설정

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

## 2. 테스트 설정 (application-test.yml)

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

## 3. Test Fixtures (Gradle testFixtures 플러그인)

WellMeet-Backend 프로젝트는 Gradle의 `java-test-fixtures` 플러그인을 사용하여 테스트 데이터 생성 코드를 모듈 간 재사용할 수 있도록 구성합니다.

### testFixtures 적용 모듈

- `domain-reservation` → 예약, 예약 가능 날짜 생성
- `domain-member` → 회원, 즐겨찾기 생성
- `domain-owner` → 사업자 생성
- `domain-restaurant` → 식당, 메뉴 생성

### build.gradle 설정

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

### 디렉토리 구조

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

### Generator 패턴 구현 예시

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

### API 모듈에서 사용 예시

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

### testFixtures의 장점

1. **재사용성**: 여러 모듈에서 동일한 테스트 데이터 생성 로직 공유
2. **일관성**: 도메인 객체 생성 방식이 중앙화되어 일관성 유지
3. **유지보수**: 도메인 모델 변경 시 testFixtures만 수정하면 됨
4. **캡슐화**: 도메인 지식을 testFixtures에 캡슐화
5. **독립성**: 각 도메인 모듈이 자신의 testFixtures 제공

### 주의사항

- testFixtures는 **테스트 전용**이며, 프로덕션 코드에서 사용 불가
- testFixtures 간 의존성은 최소화 (순환 의존성 방지)
- Repository를 주입받아 실제 DB에 저장하는 방식 사용
- 복잡한 비즈니스 로직은 testFixtures에 포함하지 않음

---

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
