package com.wellmeet.domain.review;

import com.wellmeet.domain.review.dto.ReviewResponse;
import com.wellmeet.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantReviewApplicationService {

    private final ReviewRepository reviewRepository;

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
