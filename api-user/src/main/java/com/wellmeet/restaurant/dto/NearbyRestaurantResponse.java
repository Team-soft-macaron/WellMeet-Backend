package com.wellmeet.restaurant.dto;

import com.wellmeet.common.dto.RestaurantDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NearbyRestaurantResponse {

    private String id;
    private String name;
    private String address;
    private double distance;
    private double rating;
    private String thumbnail;

    public NearbyRestaurantResponse(RestaurantDTO restaurant, double distance, double rating) {
        this.id = restaurant.id();
        this.name = restaurant.name();
        this.address = restaurant.address();
        this.distance = distance;
        this.rating = rating;
        this.thumbnail = restaurant.thumbnail();
    }
}
