package com.wellmeet.global.event;

import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerEventPublishBffService {

    private final ApplicationEventPublisher eventPublisher;

    public void publishRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }

    public void publishReservationConfirmedEvent(ReservationConfirmedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
