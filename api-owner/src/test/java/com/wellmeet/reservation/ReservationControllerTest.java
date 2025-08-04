package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationControllerTest extends BaseControllerTest {

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner);
            Restaurant restaurant2 = restaurantGenerator.generate("restaurant2", owner);
            AvailableDate availableDate1 = availableDateGenerator.generate(LocalDateTime.now(), 10, restaurant1);
            AvailableDate availableDate2 = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10,
                    restaurant2);
            Member member1 = memberGenerator.generate("mem1");
            Member member2 = memberGenerator.generate("mem2");
            reservationGenerator.generate(restaurant1, availableDate1, member1, 2);
            reservationGenerator.generate(restaurant1, availableDate1, member2, 4);
            reservationGenerator.generate(restaurant2, availableDate2, member1, 3);

            ReservationResponse[] reservationResponses = given()
                    .pathParam("restaurantId", restaurant1.getId())
                    .queryParam("ownerId", owner.getId())
                    .when().get("/owner/reservation/{restaurantId}")
                    .then().statusCode(200)
                    .extract().as(ReservationResponse[].class);

            assertThat(reservationResponses).hasSize(2);
        }
    }
}
