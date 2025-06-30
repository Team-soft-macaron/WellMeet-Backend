package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.Restaurant;
import lombok.Getter;

@Getter
public class RecommendRestaurantResponse {

    private final Long id;
    private final String name;
    private final String address;

    public RecommendRestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
    }
}
