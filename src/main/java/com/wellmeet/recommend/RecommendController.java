package com.wellmeet.recommend;

import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.recommend.restaurant.dto.RestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/api/restaurants/recommend")
    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            @RequestParam(value = "vibe") VibeName vibeName,
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return recommendService.getRecommendRestaurants(vibeName, latitude, longitude);
    }

    @GetMapping("/api/restaurants/nearby")
    public List<RecommendRestaurantResponse> getNearbyRestaurants(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return recommendService.getNearbyRestaurants(latitude, longitude);
    }

    @GetMapping("/api/restaurant/{id}")
    public RestaurantResponse getRestaurant(@PathVariable Long id) {
        return recommendService.getRestaurant(id);
    }
}
