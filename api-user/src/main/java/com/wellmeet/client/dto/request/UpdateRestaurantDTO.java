package com.wellmeet.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRestaurantDTO {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String thumbnail;
}
