package com.wellmeet.favorite.dto;

import com.wellmeet.restaurant.domain.Restaurant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FavoriteRestaurantResponse {

    private UUID id;
    private String name;
    private String address;
    private double rating;
    private String thumbnail;

    public FavoriteRestaurantResponse(Restaurant restaurant, double rating) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.rating = rating;
        this.thumbnail = restaurant.getThumbnail();
    }
}
