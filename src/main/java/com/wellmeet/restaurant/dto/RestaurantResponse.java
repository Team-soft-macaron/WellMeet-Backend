package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RestaurantResponse {

    private Long id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String thumbnail;
    private List<MenuResponse> menus;
    private List<ReviewResponse> reviews;

    public RestaurantResponse(Restaurant restaurant, List<ReviewResponse> reviews, List<MenuResponse> menus) {
        this.id = restaurant.getId();
        this.name = restaurant.getName();
        this.address = restaurant.getAddress();
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.thumbnail = restaurant.getThumbnail();
        this.reviews = reviews;
        this.menus = menus;
    }
}
