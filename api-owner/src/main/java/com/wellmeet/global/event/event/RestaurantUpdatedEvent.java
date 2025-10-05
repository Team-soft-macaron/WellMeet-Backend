package com.wellmeet.global.event.event;

import lombok.Getter;

@Getter
public class RestaurantUpdatedEvent {

    private final String restaurantId;

    public RestaurantUpdatedEvent(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}
