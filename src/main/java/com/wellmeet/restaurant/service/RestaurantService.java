package com.wellmeet.restaurant.service;

import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.RecommendRestaurantRequest;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RecommendRestaurantResponse> getRecommendRestaurants(VibeName vibeName) {
        String requestedVibeName = vibeName.name();
        return restaurantRepository.findRestaurantsOrderedByVibeRatio(requestedVibeName).stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }
}
