package com.wellmeet.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRestaurantResponse {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String thumbnail;
}
