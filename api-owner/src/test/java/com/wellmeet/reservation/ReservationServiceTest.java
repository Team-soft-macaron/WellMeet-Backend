package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
            Member member1 = createMember("Test Member");
            Member member2 = createMember("Test Member 2");
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
        return new Restaurant(name, "description", "address", 132.1, 37.1, "thumbnail", new Owner("name", "email"));
    }

    private AvailableDate createAvailableDate(LocalDateTime dateTime, int capacity, Restaurant restaurant) {
        return new AvailableDate(dateTime.toLocalDate(), dateTime.toLocalTime(), capacity, restaurant);
    }

    private Member createMember(String name) {
        return new Member(name, "nickname", "email@email.com", "phone");
    }

    private Reservation createReservation(Restaurant restaurant, AvailableDate availableDate, Member member,
                                          int partySize) {
        return new Reservation("purpose", restaurant, availableDate, member, partySize, "request");
    }
}
