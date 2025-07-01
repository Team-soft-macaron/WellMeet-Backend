package com.wellmeet.restaurant.controller;

import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.RecommendRestaurantRequest;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/api/restaurant/recommend/{vibeName}")
    public List<RecommendRestaurantResponse> getRecommendRestaurants(@PathVariable("vibeName") VibeName vibeName) {
        return restaurantService.getRecommendRestaurants(vibeName);
    }
}
