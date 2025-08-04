package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ReservationControllerTest extends BaseControllerTest {

    @Nested
    class Reserve {

        @Test
        void 예약을_생성할_수_있다() {
            Member member = memberGenerator.generate("member");
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            int partySize = 4;
            String specialRequest = "request";

            CreateReservationRequest request = new CreateReservationRequest(restaurant.getId(), availableDate.getId(),
                    partySize, specialRequest);

            CreateReservationResponse response = given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .body(request)
                    .when().post("/user/reservation")
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(CreateReservationResponse.class);

            assertAll(
                    () -> assertThat(response.getRestaurantName()).isEqualTo(restaurant.getName()),
                    () -> assertThat(response.getPartySize()).isEqualTo(partySize),
                    () -> assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING),
                    () -> assertThat(response.getSpecialRequest()).isEqualTo(specialRequest)
            );
        }

        @Test
        void 레스토랑_id는_null일_수_없다() {
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            Member member = memberGenerator.generate("member");

            CreateReservationRequest request = new CreateReservationRequest(null, availableDate.getId(),
                    4, "request");

            given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .body(request)
                    .when().post("/user/reservation")
                    .then().statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void 예약_가능_시간_id는_null일_수_없다() {
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10, restaurant);
            Member member = memberGenerator.generate("member");

            CreateReservationRequest request = new CreateReservationRequest(restaurant.getId(), null,
                    4, "request");

            given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .body(request)
                    .when().post("/user/reservation")
                    .then().statusCode(HttpStatus.BAD_REQUEST.value());
        }
    }

    @Nested
    class GetReservations {

        @Test
        void 멤버의_예약_목록을_조회할_수_있다() {
            Member member = memberGenerator.generate("member");
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            AvailableDate availableDate2 = availableDateGenerator.generate(LocalDateTime.now().plusDays(2), 10,
                    restaurant);
            reservationGenerator.generate(restaurant, availableDate, member, 4);
            reservationGenerator.generate(restaurant, availableDate2, member, 2);

            SummaryReservationResponse[] reservationResponses = given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .when().get("/user/reservation")
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(SummaryReservationResponse[].class);

            assertThat(reservationResponses).hasSize(2);
        }
    }

    @Nested
    class GetReservation {

        @Test
        void 예약_상세_내역을_조회할_수_있다() {
            Member member = memberGenerator.generate("member");
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            Reservation reservation = reservationGenerator.generate(restaurant, availableDate, member, 4);

            ReservationResponse response = given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .when().get("/user/reservation/{reservationId}", reservation.getId())
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(ReservationResponse.class);

            assertThat(response.getId()).isEqualTo(reservation.getId());
        }
    }

    @Nested
    class UpdateReservation {

        @Test
        void 예약을_업데이트_할_수_있다() {
            Member member = memberGenerator.generate("member");
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            Reservation reservation = reservationGenerator.generate(restaurant, availableDate, member, 4);

            CreateReservationRequest request = new CreateReservationRequest(restaurant.getId(), availableDate.getId(),
                    6, "updated request");

            CreateReservationResponse response = given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .body(request)
                    .when().put("/user/reservation/{reservationId}", reservation.getId())
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(CreateReservationResponse.class);

            assertAll(
                    () -> assertThat(response.getId()).isEqualTo(reservation.getId()),
                    () -> assertThat(response.getPartySize()).isEqualTo(6),
                    () -> assertThat(response.getSpecialRequest()).isEqualTo("updated request")
            );
        }
    }

    @Nested
    class CancelReservation {

        @Test
        void 예약을_취소할_수_있다() {
            Member member = memberGenerator.generate("member");
            Owner owner = ownerGenerator.generate("owner");
            Restaurant restaurant = restaurantGenerator.generate("restaurant", owner);
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant);
            Reservation reservation = reservationGenerator.generate(restaurant, availableDate, member, 4);

            given()
                    .contentType("application/json")
                    .queryParam("memberId", member.getId())
                    .when().delete("/user/reservation/{reservationId}", reservation.getId())
                    .then().statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
