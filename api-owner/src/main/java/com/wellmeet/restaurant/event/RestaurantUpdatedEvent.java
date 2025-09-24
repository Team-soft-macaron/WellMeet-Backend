package com.wellmeet.restaurant.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RestaurantUpdatedEvent {

    private final String restaurantId;
}
