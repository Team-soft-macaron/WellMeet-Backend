package com.wellmeet.domain.member.dto;

import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import java.time.LocalDateTime;

public record FavoriteRestaurantResponse(
        Long id,
        String memberId,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static FavoriteRestaurantResponse from(FavoriteRestaurant favoriteRestaurant) {
        return new FavoriteRestaurantResponse(
                favoriteRestaurant.getId(),
                favoriteRestaurant.getMemberId(),
                favoriteRestaurant.getRestaurantId(),
                favoriteRestaurant.getCreatedAt(),
                favoriteRestaurant.getUpdatedAt()
        );
    }
}
