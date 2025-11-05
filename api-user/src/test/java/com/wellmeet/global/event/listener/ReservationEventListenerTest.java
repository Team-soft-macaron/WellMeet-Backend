package com.wellmeet.global.event.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.wellmeet.client.dto.ReservationDTO;
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
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            reservationEventListener.handleReservationCreated(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(reservation.getRestaurantId()),
                    any(ReservationCreatedPayload.class)
            );
        }
    }

    @Nested
    class HandleReservationUpdated {

        @Test
        void 예약_수정_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationUpdatedEvent event = new ReservationUpdatedEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            reservationEventListener.handleReservationUpdated(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(reservation.getRestaurantId()),
                    any(ReservationUpdatedPayload.class)
            );
        }
    }

    @Nested
    class HandleReservationCanceled {

        @Test
        void 예약_취소_이벤트를_처리하여_Kafka로_알림_메시지를_발송한다() {
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationCanceledEvent event = new ReservationCanceledEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            reservationEventListener.handleReservationCanceled(event);

            verify(kafkaProducerService).sendNotificationMessage(
                    eq(reservation.getRestaurantId()),
                    any(ReservationCanceledPayload.class)
            );
        }
    }

    private ReservationDTO createReservationDTO() {
        return ReservationDTO.builder()
                .id(1L)
                .restaurantId("restaurant-1")
                .availableDateId(1L)
                .memberId("member-1")
                .partySize(4)
                .specialRequest("창가 자리 부탁드립니다")
                .status("CONFIRMED")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
