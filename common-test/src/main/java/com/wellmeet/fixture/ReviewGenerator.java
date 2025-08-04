package com.wellmeet.fixture;

import com.wellmeet.domain.member.entity.Member;
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

    public Review generate(int rating, Restaurant restaurant, Member member) {
        Review review = new Review("content", rating, Situation.DATE, restaurant, member);
        return reviewRepository.save(review);
    }
}
