package com.wellmeet.global.event;

import static org.mockito.Mockito.verify;

import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class EventPublishServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EventPublishService eventPublishService;

    @Nested
    class PublishReservationCreatedEvent {

        @Test
        void 예약_생성_이벤트를_발행한다() {
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationCreatedEvent event = new ReservationCreatedEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            eventPublishService.publishReservationCreatedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishReservationUpdatedEvent {

        @Test
        void 예약_수정_이벤트를_발행한다() {
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationUpdatedEvent event = new ReservationUpdatedEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            eventPublishService.publishReservationUpdatedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishReservationCanceledEvent {

        @Test
        void 예약_취소_이벤트를_발행한다() {
            ReservationDTO reservation = createReservationDTO();
            LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
            ReservationCanceledEvent event = new ReservationCanceledEvent(
                    reservation, "홍길동", "맛집", dateTime
            );

            eventPublishService.publishReservationCanceledEvent(event);

            verify(eventPublisher).publishEvent(event);
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
