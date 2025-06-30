package com.wellmeet.restaurant.controller;

import com.wellmeet.restaurant.dto.RecommendRestaurantRequest;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/api/restaurant/recommend")
    public List<RecommendRestaurantResponse> getRecommendRestaurants(@RequestBody RecommendRestaurantRequest request) {
        return restaurantService.getRecommendRestaurants(request);
    }
}
