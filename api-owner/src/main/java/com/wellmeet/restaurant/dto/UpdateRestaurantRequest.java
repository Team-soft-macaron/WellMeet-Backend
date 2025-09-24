package com.wellmeet.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRestaurantRequest {
    
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String thumbnail;
}
