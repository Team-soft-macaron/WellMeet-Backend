package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.MemberIdsRequest;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
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
class OwnerReservationBffServiceTest {

    @Mock
    private ReservationFeignClient reservationClient;

    @Mock
    private MemberFeignClient memberClient;

    @Mock
    private RestaurantFeignClient restaurantClient;

    @Mock
    private OwnerEventPublishBffService eventPublishService;

    @InjectMocks
    private OwnerReservationBffService reservationService;

    @Nested
    class GetReservations {

        @Test
        void 식당_아이디에_해당하는_예약목록을_불러온다() {
            String restaurantId = "restaurant-1";
            AvailableDateDTO availableDate = createAvailableDateDTO(1L, LocalDateTime.now(), 10, restaurantId);
            MemberDTO member1 = createMemberDTO("member-1", "Test");
            MemberDTO member2 = createMemberDTO("member-2", "Test2");
            ReservationDTO reservation1 = createReservationDTO(1L, restaurantId, availableDate.id(), member1.id(),
                    4);
            ReservationDTO reservation2 = createReservationDTO(2L, restaurantId, availableDate.id(), member2.id(),
                    2);
            List<ReservationDTO> reservations = List.of(reservation1, reservation2);

            when(reservationClient.getReservationsByRestaurant(restaurantId))
                    .thenReturn(reservations);
            when(memberClient.getMembersByIds(any(MemberIdsRequest.class)))
                    .thenReturn(List.of(member1, member2));
            when(restaurantClient.getAvailableDate(restaurantId, availableDate.id()))
                    .thenReturn(availableDate);

            List<ReservationResponse> expectedReservations = reservationService.getReservations(restaurantId);

            assertThat(expectedReservations).hasSize(reservations.size());
        }
    }

    @Nested
    class ConfirmReservation {

        @Test
        void 예약을_확정한다() {
            Long reservationId = 1L;
            String restaurantId = "restaurant-1";
            String memberId = "member-1";
            Long availableDateId = 1L;

            RestaurantDTO restaurant = createRestaurantDTO(restaurantId, "Test Restaurant");
            AvailableDateDTO availableDate = createAvailableDateDTO(availableDateId, LocalDateTime.now(), 10,
                    restaurantId);
            MemberDTO member = createMemberDTO(memberId, "Test");
            ReservationDTO reservation = createReservationDTO(reservationId, restaurantId, availableDateId, memberId,
                    4);

            when(reservationClient.getReservation(reservationId))
                    .thenReturn(reservation);
            when(memberClient.getMember(memberId))
                    .thenReturn(member);
            when(restaurantClient.getRestaurant(restaurantId))
                    .thenReturn(restaurant);
            when(restaurantClient.getAvailableDate(restaurantId, availableDateId))
                    .thenReturn(availableDate);

            reservationService.confirmReservation(reservationId);

            verify(reservationClient).confirmReservation(reservationId);
            verify(eventPublishService).publishReservationConfirmedEvent(
                    any(ReservationConfirmedEvent.class)
            );
        }
    }

    private RestaurantDTO createRestaurantDTO(String id, String name) {
        return new RestaurantDTO(
                id,
                name,
                "address",
                37.5,
                127.0,
                "thumbnail",
                "owner-1",
                null,
                null
        );
    }

    private AvailableDateDTO createAvailableDateDTO(Long id, LocalDateTime dateTime, int capacity,
                                                    String restaurantId) {
        return new AvailableDateDTO(
                id,
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                capacity,
                true,
                restaurantId,
                null,
                null
        );
    }

    private MemberDTO createMemberDTO(String id, String name) {
        return new MemberDTO(
                id,
                name,
                "nickname",
                "email@email.com",
                "010-1234-5678",
                true,
                true,
                true,
                false,
                null,
                null
        );
    }

    private ReservationDTO createReservationDTO(Long id, String restaurantId, Long availableDateId, String memberId,
                                                int partySize) {
        return new ReservationDTO(
                id,
                com.wellmeet.common.dto.ReservationStatus.PENDING,
                restaurantId,
                memberId,
                availableDateId,
                partySize,
                "request",
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
