package com.wellmeet.favorite.dto;

import com.wellmeet.recommend.restaurant.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteRestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private double rating = 4.5;
    private String thumbnail;

    public FavoriteRestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.thumbnail = restaurant.getThumbnail();
    }
}
