package com.wellmeet.restaurant;

import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owner/restaurant")
@RestController
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/{restaurantId}/operating-hours")
    public OperatingHoursResponse getOperatingHours(
            @RequestParam(value = "ownerId") String ownerId,
            @PathVariable String restaurantId
    ) {
        return restaurantService.getOperatingHours(restaurantId);
    }

    @PutMapping("/{restaurantId}/operating-hours")
    public OperatingHoursResponse updateOperatingHours(
            @RequestParam String ownerId,
            @PathVariable String restaurantId,
            @Valid @RequestBody UpdateOperatingHoursRequest request
    ) {
        return restaurantService.updateOperatingHours(restaurantId, request);
    }
}
