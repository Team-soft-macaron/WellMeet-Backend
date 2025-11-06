package com.wellmeet.restaurant.dto;

import com.wellmeet.common.dto.RestaurantDTO;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantResponse {

    private String id;
    private String name;
    private String address;
    private double rating;
    private int reviewCount;
    private boolean favorite;
    private double latitude;
    private double longitude;
    private String thumbnail;
    private List<RepresentativeMenuResponse> menus;
    private List<RepresentativeReviewResponse> reviews;

    public RestaurantResponse(
            RestaurantDTO restaurant,
            List<RepresentativeReviewResponse> reviews,
            List<RepresentativeMenuResponse> menus,
            boolean isFavorite,
            double rating
    ) {
        this.id = restaurant.id();
        this.name = restaurant.name();
        this.address = restaurant.address();
        this.latitude = restaurant.latitude();
        this.longitude = restaurant.longitude();
        this.thumbnail = restaurant.thumbnail();
        this.reviews = reviews;
        this.menus = menus;
        this.favorite = isFavorite;
        this.rating = rating;
        this.reviewCount = reviews.size();
    }
}
