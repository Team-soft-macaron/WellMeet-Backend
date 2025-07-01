package com.wellmeet.restaurant.service;

import com.wellmeet.restaurant.domain.BoundingBox;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            VibeName vibeName, double latitude, double longitude
    ) {
        String requestedVibeName = vibeName.name();
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findRestaurantsOrderedByVibeRatioWithBoundBox(requestedVibeName, boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }


    public List<RecommendRestaurantResponse> getNearbyRestaurants(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findWithBoundBox(boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }
}
