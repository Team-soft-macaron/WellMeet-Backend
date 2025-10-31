package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.domain.restaurant.dto.ReviewResponse;
import com.wellmeet.domain.restaurant.service.ReviewService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<ReviewResponse> reviews = reviewService.getReviewsByRestaurantId(restaurantId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/restaurant/{restaurantId}/average-rating")
    public ResponseEntity<Double> getAverageRating(
            @PathVariable String restaurantId
    ) {
        double averageRating = reviewService.getAverageRating(restaurantId);
        return ResponseEntity.ok(averageRating);
    }
}
