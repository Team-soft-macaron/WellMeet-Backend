package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.restaurant.dto.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ReservationControllerTest extends BaseControllerTest {

    @MockitoBean
    private ReservationService reservationService;

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            String restaurantId = "restaurant-1";
            String ownerId = "owner-1";

            ReservationResponse response1 = createReservationResponse(1L, "member-1", "Test1", 2);
            ReservationResponse response2 = createReservationResponse(2L, "member-2", "Test2", 4);

            when(reservationService.getReservations(anyString()))
                    .thenReturn(List.of(response1, response2));

            ReservationResponse[] reservationResponses = given()
                    .pathParam("restaurantId", restaurantId)
                    .queryParam("ownerId", ownerId)
                    .when().get("/owner/reservation/{restaurantId}")
                    .then().statusCode(200)
                    .extract().as(ReservationResponse[].class);

            assertThat(reservationResponses).hasSize(2);
        }
    }

    private ReservationResponse createReservationResponse(Long id, String memberId, String memberName, int partySize) {
        return new ReservationResponse(
                id,
                new ReservationResponse.CustomerSummaryResponse(memberId, memberName, "010-1234-5678", "test@test.com",
                        false),
                LocalDate.now(),
                LocalTime.of(18, 0),
                partySize,
                ReservationStatus.PENDING,
                "Special request",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
