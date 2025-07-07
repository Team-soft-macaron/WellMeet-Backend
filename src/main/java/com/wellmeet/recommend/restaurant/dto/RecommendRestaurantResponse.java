package com.wellmeet.recommend.restaurant.dto;

import com.wellmeet.recommend.restaurant.domain.Restaurant;
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

    public RecommendRestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.distance = 500.3;
        this.rating = 4.6;
        this.thumbnail = restaurant.getThumbnail();
    }
}
