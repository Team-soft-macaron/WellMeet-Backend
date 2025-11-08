package com.wellmeet.domain.businesshour;

import com.wellmeet.common.dto.BusinessHourDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business-hours")
@RequiredArgsConstructor
public class RestaurantBusinessHourController {

    private final RestaurantBusinessHourApplicationService businessHourService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<BusinessHourDTO>> getBusinessHoursByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<BusinessHourDTO> businessHours = businessHourService.getBusinessHoursByRestaurantId(restaurantId);
        return ResponseEntity.ok(businessHours);
    }
}
