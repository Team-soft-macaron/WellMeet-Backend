package com.wellmeet.favorite.dto;

import com.wellmeet.common.dto.RestaurantDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@lombok.Builder
@lombok.AllArgsConstructor
public class FavoriteRestaurantResponse {

    private String id;
    private String name;
    private String address;
    private double rating;
    private String thumbnail;

    public FavoriteRestaurantResponse(RestaurantDTO restaurant, double rating) {
        this.id = restaurant.id();
        this.name = restaurant.name();
        this.address = restaurant.address();
        this.rating = rating;
        this.thumbnail = restaurant.thumbnail();
    }
}
