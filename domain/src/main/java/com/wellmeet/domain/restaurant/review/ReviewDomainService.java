package com.wellmeet.domain.restaurant.review;

import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewDomainService {

    private final ReviewRepository reviewRepository;

    public double getAverageRating(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
    }

    public List<Review> getByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
}
