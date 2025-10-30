package com.wellmeet.domain.fixture;

import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.entity.Situation;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import org.springframework.stereotype.Component;

@Component
public class ReviewGenerator {

    private final ReviewRepository reviewRepository;

    public ReviewGenerator(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review generate(int rating, Restaurant restaurant, String memberId) {
        Review review = new Review("content", rating, Situation.DATE, restaurant, memberId);
        return reviewRepository.save(review);
    }
}
