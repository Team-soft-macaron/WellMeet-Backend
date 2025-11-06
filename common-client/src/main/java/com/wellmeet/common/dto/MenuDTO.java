package com.wellmeet.common.dto;

import java.time.LocalDateTime;

public record MenuDTO(
        Long id,
        String name,
        String description,
        int price,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
