package com.wellmeet.restaurant.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantUpdateEvent {
    private final String restaurantId;
}
