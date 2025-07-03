package com.wellmeet.recommend.review.service;

import com.wellmeet.recommend.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.recommend.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<RepresentativeReviewResponse> findByRestaurantId(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(RepresentativeReviewResponse::new)
                .toList();
    }
}
