package com.wellmeet.restaurant.controller;

import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import com.wellmeet.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/api/restaurants/recommend")
    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            @RequestParam(value = "vibe") VibeName vibeName,
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return restaurantService.getRecommendRestaurants(vibeName, latitude, longitude);
    }

    @GetMapping("/api/restaurants/nearby")
    public List<RecommendRestaurantResponse> getNearbyRestaurants(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return restaurantService.getNearbyRestaurants(latitude, longitude);
    }

    @GetMapping("/api/restaurant/{id}")
    public RestaurantResponse getRestaurant(@PathVariable Long id) {
        return restaurantService.getRestaurant(id);
    }
}
