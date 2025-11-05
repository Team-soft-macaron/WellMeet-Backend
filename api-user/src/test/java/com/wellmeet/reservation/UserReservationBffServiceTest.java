package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MemberDTO;
import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.CreateReservationDTO;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.client.dto.request.UpdateReservationDTO;
import com.wellmeet.global.event.UserEventPublishBffService;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserReservationBffServiceTest {

    @Mock
    private ReservationFeignClient reservationClient;

    @Mock
    private MemberFeignClient memberClient;

    @Mock
    private RestaurantFeignClient restaurantClient;

    @Mock
    private RestaurantAvailableDateFeignClient availableDateClient;

    @Mock
    private ReservationRedisService reservationRedisService;

    @Mock
    private UserEventPublishBffService eventPublishService;

    @InjectMocks
    private UserReservationBffService reservationService;

    @Nested
    class Reserve {

        @Test
        void 예약을_생성한다() {
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", 1L, 4, "창가 자리 부탁드립니다"
            );

            MemberDTO member = createMemberDTO(memberId, "홍길동");
            RestaurantDTO restaurant = createRestaurantDTO("restaurant-1", "맛집");
            AvailableDateDTO availableDate = createAvailableDateDTO(
                    1L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10
            );
            ReservationDTO createdReservation = createReservationDTO(
                    1L, memberId, "restaurant-1", 1L, 4, "PENDING"
            );

            when(reservationClient.getReservationsByMember(memberId)).thenReturn(List.of());
            when(memberClient.getMember(memberId)).thenReturn(member);
            when(restaurantClient.getRestaurant("restaurant-1")).thenReturn(restaurant);
            when(restaurantClient.getAvailableDate("restaurant-1", 1L)).thenReturn(availableDate);
            when(reservationClient.createReservation(any(CreateReservationDTO.class)))
                    .thenReturn(createdReservation);

            CreateReservationResponse response = reservationService.reserve(memberId, request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getRestaurantName()).isEqualTo("맛집");
            assertThat(response.getPartySize()).isEqualTo(4);
            verify(availableDateClient).decreaseCapacity(any(DecreaseCapacityRequest.class));
            verify(eventPublishService).publishReservationCreatedEvent(any());
        }

        @Test
        void 이미_예약된_날짜는_중복_예약할_수_없다() {
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", 1L, 4, "창가 자리 부탁드립니다"
            );

            ReservationDTO existingReservation = createReservationDTO(
                    1L, memberId, "restaurant-1", 1L, 2, "CONFIRMED"
            );

            when(reservationClient.getReservationsByMember(memberId))
                    .thenReturn(List.of(existingReservation));

            assertThatThrownBy(() -> reservationService.reserve(memberId, request))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("이미 예약된 날짜입니다.");

            verify(availableDateClient, never()).decreaseCapacity(any());
            verify(reservationClient, never()).createReservation(any());
        }
    }

    @Nested
    class GetReservations {

        @Test
        void 회원의_예약_목록을_조회한다() {
            String memberId = "member-1";
            ReservationDTO reservation1 = createReservationDTO(
                    1L, memberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );
            ReservationDTO reservation2 = createReservationDTO(
                    2L, memberId, "restaurant-2", 2L, 2, "PENDING"
            );

            RestaurantDTO restaurant1 = createRestaurantDTO("restaurant-1", "식당1");
            RestaurantDTO restaurant2 = createRestaurantDTO("restaurant-2", "식당2");
            AvailableDateDTO availableDate1 = createAvailableDateDTO(
                    1L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10
            );
            AvailableDateDTO availableDate2 = createAvailableDateDTO(
                    2L, LocalDate.now().plusDays(2), LocalTime.of(19, 0), 10
            );

            when(reservationClient.getReservationsByMember(memberId))
                    .thenReturn(List.of(reservation1, reservation2));
            when(restaurantClient.getRestaurantsByIds(any()))
                    .thenReturn(List.of(restaurant1, restaurant2));
            when(restaurantClient.getAvailableDate("restaurant-1", 1L)).thenReturn(availableDate1);
            when(restaurantClient.getAvailableDate("restaurant-2", 2L)).thenReturn(availableDate2);

            List<SummaryReservationResponse> result = reservationService.getReservations(memberId);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(1).getId()).isEqualTo(2L);
        }

        @Test
        void 예약이_없으면_빈_리스트를_반환한다() {
            String memberId = "member-1";

            when(reservationClient.getReservationsByMember(memberId)).thenReturn(List.of());

            List<SummaryReservationResponse> result = reservationService.getReservations(memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetReservation {

        @Test
        void 예약_상세_정보를_조회한다() {
            Long reservationId = 1L;
            String memberId = "member-1";
            ReservationDTO reservation = createReservationDTO(
                    reservationId, memberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );
            RestaurantDTO restaurant = createRestaurantDTO("restaurant-1", "맛집");
            AvailableDateDTO availableDate = createAvailableDateDTO(
                    1L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10
            );

            when(reservationClient.getReservation(reservationId)).thenReturn(reservation);
            when(restaurantClient.getRestaurant("restaurant-1")).thenReturn(restaurant);
            when(restaurantClient.getAvailableDate("restaurant-1", 1L)).thenReturn(availableDate);
            when(restaurantClient.getAverageRating("restaurant-1")).thenReturn(4.5);

            ReservationResponse response = reservationService.getReservation(reservationId, memberId);

            assertThat(response.getId()).isEqualTo(reservationId);
            assertThat(response.getRestaurantName()).isEqualTo("맛집");
            assertThat(response.getRestaurantRating()).isEqualTo(4.5);
        }

        @Test
        void 본인의_예약이_아니면_조회할_수_없다() {
            Long reservationId = 1L;
            String memberId = "member-1";
            String otherMemberId = "member-2";
            ReservationDTO reservation = createReservationDTO(
                    reservationId, otherMemberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );

            when(reservationClient.getReservation(reservationId)).thenReturn(reservation);

            assertThatThrownBy(() -> reservationService.getReservation(reservationId, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("권한이 없습니다.");
        }
    }

    @Nested
    class UpdateReservation {

        @Test
        void 예약을_수정한다() {
            Long reservationId = 1L;
            String memberId = "member-1";
            CreateReservationRequest request = new CreateReservationRequest(
                    "restaurant-1", 2L, 6, "수정된 요청사항"
            );

            ReservationDTO existingReservation = createReservationDTO(
                    reservationId, memberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );
            AvailableDateDTO oldAvailableDate = createAvailableDateDTO(
                    1L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10
            );
            AvailableDateDTO newAvailableDate = createAvailableDateDTO(
                    2L, LocalDate.now().plusDays(2), LocalTime.of(19, 0), 10
            );
            ReservationDTO updatedReservation = createReservationDTO(
                    reservationId, memberId, "restaurant-1", 2L, 6, "CONFIRMED"
            );
            MemberDTO member = createMemberDTO(memberId, "홍길동");
            RestaurantDTO restaurant = createRestaurantDTO("restaurant-1", "맛집");

            when(reservationClient.getReservation(reservationId)).thenReturn(existingReservation);
            when(restaurantClient.getAvailableDate("restaurant-1", 2L)).thenReturn(newAvailableDate);
            when(restaurantClient.getAvailableDate("restaurant-1", 1L)).thenReturn(oldAvailableDate);
            when(reservationClient.updateReservation(any(Long.class), any(UpdateReservationDTO.class)))
                    .thenReturn(updatedReservation);
            when(memberClient.getMember(memberId)).thenReturn(member);
            when(restaurantClient.getRestaurant("restaurant-1")).thenReturn(restaurant);

            CreateReservationResponse response = reservationService.updateReservation(
                    reservationId, memberId, request
            );

            assertThat(response.getId()).isEqualTo(reservationId);
            assertThat(response.getPartySize()).isEqualTo(6);
            verify(availableDateClient).increaseCapacity(any());
            verify(availableDateClient).decreaseCapacity(any());
            verify(eventPublishService).publishReservationUpdatedEvent(any());
        }
    }

    @Nested
    class Cancel {

        @Test
        void 예약을_취소한다() {
            Long reservationId = 1L;
            String memberId = "member-1";
            ReservationDTO reservation = createReservationDTO(
                    reservationId, memberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );
            AvailableDateDTO availableDate = createAvailableDateDTO(
                    1L, LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10
            );
            MemberDTO member = createMemberDTO(memberId, "홍길동");
            RestaurantDTO restaurant = createRestaurantDTO("restaurant-1", "맛집");

            when(reservationClient.getReservation(reservationId)).thenReturn(reservation);
            when(restaurantClient.getAvailableDate("restaurant-1", 1L)).thenReturn(availableDate);
            when(memberClient.getMember(memberId)).thenReturn(member);
            when(restaurantClient.getRestaurant("restaurant-1")).thenReturn(restaurant);

            reservationService.cancel(reservationId, memberId);

            verify(availableDateClient).increaseCapacity(any());
            verify(reservationClient).cancelReservation(reservationId);
            verify(eventPublishService).publishReservationCanceledEvent(any());
        }

        @Test
        void 본인의_예약이_아니면_취소할_수_없다() {
            Long reservationId = 1L;
            String memberId = "member-1";
            String otherMemberId = "member-2";
            ReservationDTO reservation = createReservationDTO(
                    reservationId, otherMemberId, "restaurant-1", 1L, 4, "CONFIRMED"
            );

            when(reservationClient.getReservation(reservationId)).thenReturn(reservation);

            assertThatThrownBy(() -> reservationService.cancel(reservationId, memberId))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("권한이 없습니다.");

            verify(availableDateClient, never()).increaseCapacity(any());
            verify(reservationClient, never()).cancelReservation(any());
        }
    }

    private MemberDTO createMemberDTO(String id, String name) {
        return MemberDTO.builder()
                .id(id)
                .name(name)
                .nickname(name + "_nick")
                .email(name + "@test.com")
                .phone("010-1234-5678")
                .build();
    }

    private RestaurantDTO createRestaurantDTO(String id, String name) {
        return RestaurantDTO.builder()
                .id(id)
                .name(name)
                .address("서울시 강남구")
                .latitude(37.5)
                .longitude(127.0)
                .thumbnail("thumbnail.jpg")
                .ownerId("owner-1")
                .build();
    }

    private AvailableDateDTO createAvailableDateDTO(Long id, LocalDate date, LocalTime time, int capacity) {
        return AvailableDateDTO.builder()
                .id(id)
                .date(date)
                .time(time)
                .maxCapacity(capacity)
                .restaurantId("restaurant-1")
                .build();
    }

    private ReservationDTO createReservationDTO(
            Long id, String memberId, String restaurantId, Long availableDateId, int partySize, String status
    ) {
        return ReservationDTO.builder()
                .id(id)
                .memberId(memberId)
                .restaurantId(restaurantId)
                .availableDateId(availableDateId)
                .partySize(partySize)
                .specialRequest("요청사항")
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
