package com.wellmeet.restaurant.review;

import com.wellmeet.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.restaurant.review.domain.Review;
import com.wellmeet.restaurant.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<RepresentativeReviewResponse> findByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(RepresentativeReviewResponse::new)
                .toList();
    }

    public double getAverageRating(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
