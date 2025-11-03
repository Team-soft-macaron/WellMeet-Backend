package com.wellmeet.favorite.dto;

import com.wellmeet.client.dto.RestaurantDTO;
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
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.rating = rating;
        this.thumbnail = restaurant.getThumbnail();
    }
}
