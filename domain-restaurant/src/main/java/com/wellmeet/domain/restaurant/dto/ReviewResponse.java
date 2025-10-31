package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.entity.Situation;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String content,
        double rating,
        Situation situation,
        String restaurantId,
        String memberId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getSituation(),
                review.getRestaurant().getId(),
                review.getMemberId(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
