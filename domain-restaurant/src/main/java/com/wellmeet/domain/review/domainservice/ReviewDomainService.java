package com.wellmeet.domain.review.domainservice;

import com.wellmeet.domain.review.entity.Review;
import com.wellmeet.domain.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewDomainService {

    private final ReviewRepository reviewRepository;

    public double getAverageRating(String restaurantId) {
        return reviewRepository.getAverageRating(restaurantId);
    }

    public List<Review> getByRestaurantId(String restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId);
    }
}
