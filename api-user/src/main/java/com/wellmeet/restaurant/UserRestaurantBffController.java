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
public class UserRestaurantBffController {

    private final UserRestaurantBffService restaurantService;

    @GetMapping("/nearby")
    public List<NearbyRestaurantResponse> getNearbyRestaurants(
            @RequestParam(value = "latitude") double latitude,
            @RequestParam(value = "longitude") double longitude
    ) {
        return restaurantService.findWithNearbyRestaurant(latitude, longitude);
    }

    @GetMapping("/{restaurantId}")
    public RestaurantResponse getRestaurant(
            @RequestParam(value = "memberId") String memberId,
            @PathVariable String restaurantId
    ) {
        return restaurantService.getRestaurant(restaurantId, memberId);
    }

    @GetMapping("/{restaurantId}/available")
    public List<AvailableDateResponse> getRestaurantAvailableDates(
            @PathVariable String restaurantId
    ) {
        return restaurantService.getRestaurantAvailableDates(restaurantId);
    }
}
