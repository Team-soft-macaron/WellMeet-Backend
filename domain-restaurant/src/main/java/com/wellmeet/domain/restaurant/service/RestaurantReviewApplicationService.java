package com.wellmeet.domain.restaurant.service;

import com.wellmeet.domain.restaurant.dto.ReviewResponse;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantReviewApplicationService {

    private final ReviewRepository reviewRepository;

    public RestaurantReviewApplicationService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public List<ReviewResponse> getReviewsByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public double getAverageRating(String restaurantId) {
        return reviewRepository.getAverageRating(restaurantId);
    }
}
