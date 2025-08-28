package com.wellmeet.global.event;

import com.wellmeet.global.event.event.ReservationCreatedEvent;
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
}
