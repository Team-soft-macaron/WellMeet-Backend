package com.wellmeet.global.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import com.wellmeet.kafka.dto.payload.ReservationCanceledPayload;
import com.wellmeet.kafka.dto.payload.ReservationCreatedPayload;
import com.wellmeet.kafka.dto.payload.ReservationUpdatedPayload;
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
    class HandleReservationCreated {

        @Test
        void 예약_생성_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = reservation.getRestaurant();
            ReservationCreatedEvent event = new ReservationCreatedEvent(reservation, "member");

            reservationEventListener.handleReservationCreated(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(restaurant.getId()),
                    any(ReservationCreatedPayload.class)
            );
        }
    }

    @Nested
    class HandleReservationUpdated {

        @Test
        void 예약_수정_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = reservation.getRestaurant();
            ReservationUpdatedEvent event = new ReservationUpdatedEvent(reservation, "member");

            reservationEventListener.handleReservationUpdated(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(restaurant.getId()),
                    any(ReservationUpdatedPayload.class)
            );
        }
    }

    @Nested
    class HandleReservationCanceled {

        @Test
        void 예약_취소_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = reservation.getRestaurant();
            ReservationCanceledEvent event = new ReservationCanceledEvent(reservation, "member");

            reservationEventListener.handleReservationCanceled(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(restaurant.getId()),
                    any(ReservationCanceledPayload.class)
            );
        }
    }

    private Reservation createReservation() {
        Owner owner = new Owner("owner", "owner@test.com");
        Restaurant restaurant = new Restaurant(
                "식당",
                "description",
                "서울시 강남구",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner.getId()
        );
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        AvailableDate availableDate = new AvailableDate(
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                10,
                restaurant
        );
        Member member = new Member("member", "nickname", "email@test.com", "010-1234-5678");
        return new Reservation(restaurant, availableDate, member.getId(), 4, "요청사항");
    }
}
