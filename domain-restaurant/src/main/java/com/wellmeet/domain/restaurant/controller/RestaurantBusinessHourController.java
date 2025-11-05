package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.common.dto.BusinessHourDTO;
import com.wellmeet.domain.restaurant.service.RestaurantBusinessHourApplicationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-hours")
public class RestaurantBusinessHourController {

    private final RestaurantBusinessHourApplicationService businessHourService;

    public RestaurantBusinessHourController(RestaurantBusinessHourApplicationService businessHourService) {
        this.businessHourService = businessHourService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<BusinessHourDTO>> getBusinessHoursByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<BusinessHourDTO> businessHours = businessHourService.getBusinessHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(businessHours);
    }
}
