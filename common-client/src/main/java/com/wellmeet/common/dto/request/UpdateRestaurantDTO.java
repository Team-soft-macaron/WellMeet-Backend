package com.wellmeet.common.dto.request;

public record UpdateRestaurantDTO(
        String name,
        String address,
        double latitude,
        double longitude,
        String thumbnail
) {
}
