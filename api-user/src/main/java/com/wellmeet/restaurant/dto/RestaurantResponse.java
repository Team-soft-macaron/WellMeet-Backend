package com.wellmeet.restaurant.dto;

import com.wellmeet.domain.restaurant.entity.Restaurant;
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
            Restaurant restaurant,
            List<RepresentativeReviewResponse> reviews,
            List<RepresentativeMenuResponse> menus,
            boolean isFavorite,
            double rating
    ) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.thumbnail = restaurant.getThumbnail();
        this.reviews = reviews;
        this.menus = menus;
        this.favorite = isFavorite;
        this.rating = rating;
        this.reviewCount = reviews.size();
    }
}
