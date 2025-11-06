package com.wellmeet.common.dto;

import java.time.LocalDateTime;

public record FavoriteRestaurantDTO(
        Long id,
        String memberId,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
