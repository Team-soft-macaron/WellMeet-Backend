package com.wellmeet.recommend.dto;

import com.wellmeet.restaurant.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendRestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private double distance;
    private double rating;
    private String thumbnail;

    public RecommendRestaurantResponse(Restaurant restaurant, double distance, double rating) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.distance = distance;
        this.rating = rating;
        this.thumbnail = restaurant.getThumbnail();
    }
}
