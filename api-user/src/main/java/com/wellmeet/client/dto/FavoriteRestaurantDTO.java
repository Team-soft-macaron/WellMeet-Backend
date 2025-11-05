package com.wellmeet.client.dto;

public record FavoriteRestaurantDTO(
        Long id,
        String memberId,
        String restaurantId
) {
}
