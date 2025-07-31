package com.wellmeet.restaurant;

import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/restaurant")
@RestController
@RequiredArgsConstructor
public class UserRestaurantController {

    private final UserRestaurantService userRestaurantService;

    @GetMapping("/nearby")
    public List<NearbyRestaurantResponse> getNearbyRestaurants(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return userRestaurantService.findWithNearbyRestaurant(latitude, longitude);
    }

    @GetMapping("/{restaurantId}")
    public RestaurantResponse getRestaurant(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable String restaurantId
    ) {
        return userRestaurantService.getRestaurant(restaurantId, memberId);
    }

    @GetMapping("/available/{restaurantId}")
    public List<AvailableDateResponse> getRestaurantAvailableDates(
            @PathVariable String restaurantId
    ) {
        return userRestaurantService.getRestaurantAvailableDates(restaurantId);
    }
}
