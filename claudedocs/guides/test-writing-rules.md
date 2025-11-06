# 테스트 작성 규칙

> WellMeet-Backend 프로젝트의 테스트 코드 작성 규칙 및 컨벤션

## 📚 목차

1. [네이밍 컨벤션](#1-네이밍-컨벤션)
2. [주석 없이 코드로 표현](#2-주석-없이-코드로-표현)
3. [AssertJ 사용](#3-assertj-사용)
4. [예외 테스트](#4-예외-테스트)
5. [ParameterizedTest 활용](#5-parameterizedtest-활용)

---

## 1. 네이밍 컨벤션

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

## 2. 주석 없이 코드로 표현

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

## 3. AssertJ 사용

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

## 4. 예외 테스트

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

## 5. ParameterizedTest 활용

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

**최종 업데이트**: 2025-11-06
**출처**: CLAUDE.md
**버전**: v1.0

**참고**: 이 문서는 [CLAUDE.md](../../CLAUDE.md)에서 추출되었습니다.
