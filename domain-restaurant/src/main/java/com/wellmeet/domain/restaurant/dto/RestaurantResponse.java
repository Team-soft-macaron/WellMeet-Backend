package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalDateTime;

public record RestaurantResponse(
        String id,
        String name,
        String address,
        double latitude,
        double longitude,
        String thumbnail,
        String ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getThumbnail(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}
