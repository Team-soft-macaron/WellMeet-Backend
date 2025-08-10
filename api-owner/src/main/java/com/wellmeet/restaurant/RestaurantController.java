package com.wellmeet.restaurant;

import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owner/restaurant")
@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/operating-hours")
    public OperatingHoursResponse getOperatingHours(
            @RequestParam(value = "ownerId") Long ownerId
    ) {
        return restaurantService.getOperatingHours();
    }
}
