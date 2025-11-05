package com.wellmeet.client.dto;

public record ReviewDTO(
        Long id,
        String content,
        double rating,
        String situation,
        String restaurantId,
        String memberId
) {
}
