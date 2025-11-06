package com.wellmeet.common.dto;

import java.time.LocalDateTime;

public record RestaurantDTO(
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
}
