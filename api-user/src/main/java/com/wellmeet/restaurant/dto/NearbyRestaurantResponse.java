package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.Restaurant;
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

    public NearbyRestaurantResponse(Restaurant restaurant, double distance, double rating) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.distance = distance;
        this.rating = rating;
        this.thumbnail = restaurant.getThumbnail();
    }
}
