package com.wellmeet.client;

import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.BusinessHourDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.UpdateOperatingHoursDTO;
import com.wellmeet.client.dto.request.UpdateRestaurantDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-restaurant-service")
public interface RestaurantClient {

    @GetMapping("/api/restaurants/{id}")
    RestaurantDTO getRestaurant(@PathVariable("id") String id);

    @GetMapping("/api/restaurants/{restaurantId}/available-dates/{availableDateId}")
    AvailableDateDTO getAvailableDate(
            @PathVariable("restaurantId") String restaurantId,
            @PathVariable("availableDateId") Long availableDateId
    );

    @PutMapping("/api/restaurants/{id}")
    RestaurantDTO updateRestaurant(
            @PathVariable("id") String id,
            @RequestBody UpdateRestaurantDTO request
    );

    @GetMapping("/api/restaurants/{restaurantId}/operating-hours")
    List<BusinessHourDTO> getOperatingHours(@PathVariable("restaurantId") String restaurantId);

    @PutMapping("/api/restaurants/{restaurantId}/operating-hours")
    List<BusinessHourDTO> updateOperatingHours(
            @PathVariable("restaurantId") String restaurantId,
            @RequestBody UpdateOperatingHoursDTO request
    );
}
