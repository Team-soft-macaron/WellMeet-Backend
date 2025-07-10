package com.wellmeet.recommend;

import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestaurantService restaurantService;

    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            VibeName vibeName, double latitude, double longitude
    ) {
        return restaurantService.findNearbyRestaurantsOrderedByVibeRatio(vibeName, latitude, longitude);
    }
}
