package com.wellmeet.restaurant;

import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/restaurant")
@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/nearby")
    public List<NearbyRestaurantResponse> getNearbyRestaurants(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return restaurantService.findWithNearbyRestaurant(latitude, longitude);
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurant(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable UUID id
    ) {
        return restaurantService.getRestaurant(id, memberId);
    }
}
