package com.wellmeet.global.event.listener;

import static org.mockito.Mockito.verify;

import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import com.wellmeet.restaurant.RestaurantRedisService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RestaurantEventListenerTest {

    @Mock
    private RestaurantRedisService restaurantRedisService;

    @InjectMocks
    private RestaurantEventListener restaurantEventListener;

    @Nested
    class HandleRestaurantUpdate {

        @Test
        void 식당_업데이트_이벤트를_처리하여_Redis에_발행한다() {
            String restaurantId = "restaurant-1";
            RestaurantUpdatedEvent event = new RestaurantUpdatedEvent(restaurantId);

            restaurantEventListener.handleRestaurantUpdate(event);

            verify(restaurantRedisService).publish("restaurant-update", restaurantId);
        }
    }
}
