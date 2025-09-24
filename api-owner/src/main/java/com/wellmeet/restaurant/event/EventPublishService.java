package com.wellmeet.restaurant.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublishService {

    private final ApplicationEventPublisher eventPublisher;

    public void publishRestaurantUpdatedEvent(RestaurantUpdatedEvent event) {
        eventPublisher.publishEvent(event);
    }
}
