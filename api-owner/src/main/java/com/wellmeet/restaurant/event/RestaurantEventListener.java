package com.wellmeet.restaurant.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.wellmeet.restaurant.RestaurantRedisService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RestaurantEventListener {

    private final RestaurantRedisService restaurantRedisService;
    
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishRestaurantUpdate(RestaurantUpdatedEvent event){
        restaurantRedisService.publish("restaurant-update", event.getRestaurantId());
    }
}
