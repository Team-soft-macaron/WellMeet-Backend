package com.wellmeet.client.dto;

public record MenuDTO(
        Long id,
        String name,
        String description,
        int price,
        String restaurantId
) {
}
