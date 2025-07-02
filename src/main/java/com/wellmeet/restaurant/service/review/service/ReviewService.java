package com.wellmeet.restaurant.service.review.service;

import com.wellmeet.restaurant.dto.ReviewResponse;
import com.wellmeet.restaurant.repository.review.repository.ReviewRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> findByRestaurantId(Long restaurantId) {
        return reviewRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(ReviewResponse::new)
                .toList();
    }
}
