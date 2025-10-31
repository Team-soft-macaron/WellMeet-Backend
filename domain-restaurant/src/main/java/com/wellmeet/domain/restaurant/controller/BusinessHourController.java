package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.domain.restaurant.dto.BusinessHoursResponse;
import com.wellmeet.domain.restaurant.service.BusinessHourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-hours")
public class BusinessHourController {

    private final BusinessHourService businessHourService;

    public BusinessHourController(BusinessHourService businessHourService) {
        this.businessHourService = businessHourService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<BusinessHoursResponse> getBusinessHoursByRestaurant(
            @PathVariable String restaurantId
    ) {
        BusinessHoursResponse businessHours = businessHourService.getBusinessHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(businessHours);
    }
}
