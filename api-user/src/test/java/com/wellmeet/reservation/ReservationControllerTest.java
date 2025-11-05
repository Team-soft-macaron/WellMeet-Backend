package com.wellmeet.reservation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.ReservationStatus;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Nested
    class Reserve {

        @Test
        void 예약을_생성할_수_있다() throws Exception {
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", 1L, 4, "창가 자리 부탁드립니다"
            );
            CreateReservationResponse response = CreateReservationResponse.builder()
                    .id(1L)
                    .restaurantName("맛집")
                    .status(ReservationStatus.PENDING)
                    .dateTime(LocalDateTime.now().plusDays(1))
                    .partySize(4)
                    .specialRequest("창가 자리 부탁드립니다")
                    .build();

            when(reservationService.reserve(eq(memberId), any(CreateReservationRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(post("/user/reservation")
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.restaurantName").value("맛집"))
                    .andExpect(jsonPath("$.partySize").value(4))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.specialRequest").value("창가 자리 부탁드립니다"));

            verify(reservationService).reserve(eq(memberId), any(CreateReservationRequest.class));
        }

        @Test
        void 레스토랑_id는_null일_수_없다() throws Exception {
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    null, 1L, 4, "request"
            );

            mockMvc.perform(post("/user/reservation")
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        void 예약_가능_시간_id는_null일_수_없다() throws Exception {
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", null, 4, "request"
            );

            mockMvc.perform(post("/user/reservation")
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class GetReservations {

        @Test
        void 멤버의_예약_목록을_조회할_수_있다() throws Exception {
            String memberId = "member-1";
            SummaryReservationResponse response1 = SummaryReservationResponse.builder()
                    .id(1L)
                    .restaurantName("식당1")
                    .status(ReservationStatus.CONFIRMED)
                    .dateTime(LocalDateTime.now().plusDays(1))
                    .partySize(4)
                    .build();
            SummaryReservationResponse response2 = SummaryReservationResponse.builder()
                    .id(2L)
                    .restaurantName("식당2")
                    .status(ReservationStatus.PENDING)
                    .dateTime(LocalDateTime.now().plusDays(2))
                    .partySize(2)
                    .build();

            when(reservationService.getReservations(memberId))
                    .thenReturn(List.of(response1, response2));

            mockMvc.perform(get("/user/reservation")
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[1].id").value(2));

            verify(reservationService).getReservations(memberId);
        }
    }

    @Nested
    class GetReservation {

        @Test
        void 예약_상세_내역을_조회할_수_있다() throws Exception {
            String memberId = "member-1";
            Long reservationId = 1L;
            ReservationResponse response = ReservationResponse.builder()
                    .id(reservationId)
                    .restaurantName("맛집")
                    .restaurantAddress("서울시 강남구")
                    .restaurantRating(4.5)
                    .status(ReservationStatus.CONFIRMED)
                    .dateTime(LocalDateTime.now().plusDays(1))
                    .partySize(4)
                    .specialRequest("창가 자리")
                    .build();

            when(reservationService.getReservation(reservationId, memberId))
                    .thenReturn(response);

            mockMvc.perform(get("/user/reservation/{reservationId}", reservationId)
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(reservationId))
                    .andExpect(jsonPath("$.restaurantName").value("맛집"));

            verify(reservationService).getReservation(reservationId, memberId);
        }
    }

    @Nested
    class UpdateReservation {

        @Test
        void 예약을_업데이트_할_수_있다() throws Exception {
            String memberId = "member-1";
            Long reservationId = 1L;
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", 2L, 6, "수정된 요청사항"
            );
            CreateReservationResponse response = CreateReservationResponse.builder()
                    .id(reservationId)
                    .restaurantName("맛집")
                    .status(ReservationStatus.CONFIRMED)
                    .dateTime(LocalDateTime.now().plusDays(2))
                    .partySize(6)
                    .specialRequest("수정된 요청사항")
                    .build();

            when(reservationService.updateReservation(eq(reservationId), eq(memberId),
                    any(CreateReservationRequest.class)))
                    .thenReturn(response);

            mockMvc.perform(put("/user/reservation/update/{reservationId}", reservationId)
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(reservationId))
                    .andExpect(jsonPath("$.partySize").value(6))
                    .andExpect(jsonPath("$.specialRequest").value("수정된 요청사항"));

            verify(reservationService).updateReservation(eq(reservationId), eq(memberId),
                    any(CreateReservationRequest.class));
        }
    }

    @Nested
    class CancelReservation {

        @Test
        void 예약을_취소할_수_있다() throws Exception {
            String memberId = "member-1";
            Long reservationId = 1L;

            mockMvc.perform(patch("/user/reservation/cancel/{reservationId}", reservationId)
                            .queryParam("memberId", memberId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());

            verify(reservationService).cancel(reservationId, memberId);
        }
    }
}
