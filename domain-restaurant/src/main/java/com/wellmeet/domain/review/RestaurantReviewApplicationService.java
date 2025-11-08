package com.wellmeet.domain.review;

import com.wellmeet.domain.review.domainservice.ReviewDomainService;
import com.wellmeet.domain.review.dto.ReviewResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantReviewApplicationService {

    private final ReviewDomainService reviewDomainService;

    public List<ReviewResponse> getReviewsByRestaurantId(String restaurantId) {
        return reviewDomainService.getByRestaurantId(restaurantId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public double getAverageRating(String restaurantId) {
        return reviewDomainService.getAverageRating(restaurantId);
    }
}
