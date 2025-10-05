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
├── api-user/          # 사용자 API (REST Controller + Service)
├── api-owner/         # 사업자 API (REST Controller + Service)
├── domain/            # 도메인 로직 (Entity + Domain Service + Repository)
├── domain-redis/      # Redis 분산 락 서비스
├── kafka/             # Kafka Producer 서비스
├── batch-reminder/    # 예약 리마인더 배치
└── common/            # 공통 유틸리티
```

### 의존성 관계

```
api-user    →  domain, domain-redis, kafka
api-owner   →  domain, domain-redis, kafka
batch       →  domain, kafka
domain-redis → (독립)
kafka       → (독립)
```

---

## 테스트 레이어별 구성

### 1. Entity Layer (domain 모듈)

**목적**: 도메인 객체의 생성, 검증, 비즈니스 규칙 테스트

**위치**: `domain/src/test/java/com/wellmeet/domain/{aggregate}/entity/`

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

### 2. Repository Layer (domain 모듈)

**목적**: @Query 어노테이션으로 직접 작성한 커스텀 쿼리 메소드 테스트

**위치**: `domain/src/test/java/com/wellmeet/domain/{aggregate}/repository/`

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

### 3. Domain Service Layer (domain 모듈)

**목적**: 도메인 비즈니스 로직 + Repository 통합 테스트

**위치**: `domain/src/test/java/com/wellmeet/domain/{aggregate}/`

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

### 6. Redis Service Layer (domain-redis 모듈)

**목적**: 분산 락, 캐싱 로직 테스트

**위치**: `domain-redis/src/test/java/com/wellmeet/{feature}/`

**베이스 클래스**: Testcontainers 기반 통합 테스트

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

### 7. Kafka Producer Layer (kafka 모듈)

**목적**: 메시지 발송, 직렬화, 에러 처리 테스트

**위치**: `kafka/src/test/java/com/wellmeet/kafka/`

**베이스 클래스**: EmbeddedKafka 기반 통합 테스트

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
        void 1

        시간_전_예약_리마인더_배치가_성공한다() throws Exception {
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

### domain 모듈

| Layer          | 테스트 타입 | 베이스 클래스                      | 주요 검증                |
|----------------|--------|------------------------------|----------------------|
| Entity         | 단위 테스트 | 없음                           | 생성, 검증, 비즈니스 규칙      |
| Repository     | 통합 테스트 | BaseRepositoryTest           | @Query 커스텀 쿼리만       |
| Domain Service | 통합 테스트 | BaseRepositoryTest + @Import | 비즈니스 로직 + Repository |

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

### domain-redis 모듈

| Layer         | 테스트 타입 | 베이스 클래스        | 주요 검증     |
|---------------|--------|----------------|-----------|
| Redis Service | 통합 테스트 | Testcontainers | 분산 락, 동시성 |

**커버리지 목표**: 90% (Critical)

---

### kafka 모듈

| Layer    | 테스트 타입 | 베이스 클래스       | 주요 검증       |
|----------|--------|---------------|-------------|
| Producer | 통합 테스트 | EmbeddedKafka | 메시지 발송, 직렬화 |
| DTO      | 단위 테스트 | 없음            | 직렬화/역직렬화    |

**커버리지 목표**: 70%

---

### batch-reminder 모듈

| Layer      | 테스트 타입 | 베이스 클래스         | 주요 검증         |
|------------|--------|-----------------|---------------|
| Job Config | 통합 테스트 | TestBatchConfig | Job 실행 성공     |
| Processor  | 단위 테스트 | 없음              | 데이터 변환 로직     |
| Writer     | 단위/통합  | Mock/실제         | 외부 호출 (Kafka) |

**커버리지 목표**: 75%

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

hasSize(3)
    .

extracting(Restaurant::getName)
    .

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

**domain 모듈** (`domain/src/main/resources/application-domain-test.yml`):

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

**domain-redis 모듈** (`domain-redis/src/main/resources/application-domain-redis-test.yml`):

```yaml
spring:
  config:
    activate:
      on-profile: domain-redis-test
  data:
    redis:
      host: localhost
      port: 6379
```

**api-user/api-owner 모듈** (`api-user/src/main/resources/application-test.yml`):

```yaml
spring:
  config:
    import:
      - application-domain-test.yml
      - application-domain-redis-test.yml
      - application-kafka-test.yml
```

---

### 3. Test Fixtures (domain 모듈)

**Generator 패턴**:

```java

@Component
public class RestaurantGenerator {

    @Autowired
    private RestaurantRepository restaurantRepository;

    public Restaurant generate() {
        return generate("기본 식당", 37.5, 127.0);
    }

    public Restaurant generate(String name, double lat, double lon) {
        Restaurant restaurant = Restaurant.builder()
                .name(name)
                .address("서울시 강남구")
                .latitude(lat)
                .longitude(lon)
                .phoneNumber("02-1234-5678")
                .build();
        return restaurantRepository.save(restaurant);
    }
}
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

**마지막 업데이트**: 2025-10-05
