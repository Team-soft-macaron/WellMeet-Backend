package com.wellmeet.favorite.saga;

public record FavoriteAddContext(
        String memberId,
        String restaurantId
) {}
