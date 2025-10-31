package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.menu.entity.Menu;
import java.time.LocalDateTime;

public record MenuResponse(
        Long id,
        String name,
        String description,
        int price,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static MenuResponse from(Menu menu) {
        return new MenuResponse(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getRestaurant().getId(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }
}
