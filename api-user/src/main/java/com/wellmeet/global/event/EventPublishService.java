package com.wellmeet.global.event;

import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final ApplicationEventPublisher eventPublisher;

    public void publishReservationCreatedEvent(ReservationCreatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishReservationUpdatedEvent(ReservationUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishReservationCanceledEvent(ReservationCanceledEvent event) {
        eventPublisher.publishEvent(event);
    }
}
