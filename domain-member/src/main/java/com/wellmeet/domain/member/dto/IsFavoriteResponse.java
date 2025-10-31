package com.wellmeet.domain.member.dto;

public record IsFavoriteResponse(
        boolean isFavorite
) {
    public static IsFavoriteResponse of(boolean isFavorite) {
        return new IsFavoriteResponse(isFavorite);
    }
}
