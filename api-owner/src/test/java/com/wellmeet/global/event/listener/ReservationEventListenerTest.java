package com.wellmeet.global.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.kafka.dto.payload.ReservationConfirmedPayload;
import com.wellmeet.kafka.service.KafkaProducerService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationEventListenerTest {

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ReservationEventListener reservationEventListener;

    @Nested
    class HandleReservationConfirmed {

        @Test
        void 예약_확정_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            Restaurant restaurant = createRestaurant();
            AvailableDate availableDate = createAvailableDate(restaurant);
            Member member = createMember();
            Reservation reservation = createReservation(restaurant, availableDate, member);
            ReservationConfirmedEvent event = new ReservationConfirmedEvent(reservation, member.getName());

            reservationEventListener.handleReservationConfirmed(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(member.getId()),
                    any(ReservationConfirmedPayload.class)
            );
        }
    }

    private Restaurant createRestaurant() {
        Owner owner = new Owner("owner-name", "owner@email.com");
        return new Restaurant(
                "restaurant-1",
                "Test Restaurant",
                "서울시",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner.getId()
        );
    }

    private AvailableDate createAvailableDate(Restaurant restaurant) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        return new AvailableDate(dateTime.toLocalDate(), dateTime.toLocalTime(), 10, restaurant);
    }

    private Member createMember() {
        return new Member("member", "nickname", "member@email.com", "010-1234-5678");
    }

    private Reservation createReservation(Restaurant restaurant, AvailableDate availableDate, Member member) {
        return new Reservation(restaurant, availableDate, member.getId(), 4, "request");
    }
}
