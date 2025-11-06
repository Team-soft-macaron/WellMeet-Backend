package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.domain.restaurant.dto.AvailableDateIdsRequest;
import com.wellmeet.domain.restaurant.dto.DecreaseCapacityRequest;
import com.wellmeet.domain.restaurant.dto.IncreaseCapacityRequest;
import com.wellmeet.domain.restaurant.service.RestaurantAvailableDateApplicationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/available-dates")
public class RestaurantAvailableDateController {

    private final RestaurantAvailableDateApplicationService availableDateService;

    public RestaurantAvailableDateController(RestaurantAvailableDateApplicationService availableDateService) {
        this.availableDateService = availableDateService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<AvailableDateDTO>> getAvailableDatesByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<AvailableDateDTO> availableDates = availableDateService
                .getAvailableDatesByRestaurantId(restaurantId);

        return ResponseEntity.ok(availableDates);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<AvailableDateDTO>> getAvailableDatesByIds(
            @Valid @RequestBody AvailableDateIdsRequest request
    ) {
        List<AvailableDateDTO> availableDates = availableDateService
                .getAvailableDatesByIds(request.availableDateIds());

        return ResponseEntity.ok(availableDates);
    }

    @PutMapping("/decrease-capacity")
    public ResponseEntity<Void> decreaseCapacity(
            @Valid @RequestBody DecreaseCapacityRequest request
    ) {
        availableDateService.decreaseCapacity(request.availableDateId(), request.partySize());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/increase-capacity")
    public ResponseEntity<Void> increaseCapacity(
            @Valid @RequestBody IncreaseCapacityRequest request
    ) {
        availableDateService.increaseCapacity(request.availableDateId(), request.partySize());
        return ResponseEntity.ok().build();
    }
}
