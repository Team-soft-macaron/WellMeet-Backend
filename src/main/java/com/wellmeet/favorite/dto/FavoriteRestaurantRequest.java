package com.wellmeet.favorite.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteRestaurantRequest {

    private Long restaurantId;

    public FavoriteRestaurantRequest(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
