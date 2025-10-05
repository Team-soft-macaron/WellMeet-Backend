package com.wellmeet.global.event.listener;

import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import com.wellmeet.restaurant.RestaurantRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RestaurantEventListener {

    private final RestaurantRedisService restaurantRedisService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRestaurantUpdate(RestaurantUpdatedEvent event) {
        restaurantRedisService.publish("restaurant-update", event.getRestaurantId());
    }
}
