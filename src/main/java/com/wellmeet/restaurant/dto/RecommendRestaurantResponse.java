package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.Restaurant;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendRestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private String mainImage;

    public RecommendRestaurantResponse(Restaurant restaurant) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.mainImage = restaurant.getMainImage();
    }
}
