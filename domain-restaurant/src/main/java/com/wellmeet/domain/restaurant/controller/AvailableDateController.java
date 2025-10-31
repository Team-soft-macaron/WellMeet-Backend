package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.domain.restaurant.dto.AvailableDateIdsRequest;
import com.wellmeet.domain.restaurant.dto.AvailableDateResponse;
import com.wellmeet.domain.restaurant.dto.DecreaseCapacityRequest;
import com.wellmeet.domain.restaurant.dto.IncreaseCapacityRequest;
import com.wellmeet.domain.restaurant.service.AvailableDateService;
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
public class AvailableDateController {

    private final AvailableDateService availableDateService;

    public AvailableDateController(AvailableDateService availableDateService) {
        this.availableDateService = availableDateService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<AvailableDateResponse>> getAvailableDatesByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<AvailableDateResponse> availableDates = availableDateService
                .getAvailableDatesByRestaurantId(restaurantId);

        return ResponseEntity.ok(availableDates);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<AvailableDateResponse>> getAvailableDatesByIds(
            @Valid @RequestBody AvailableDateIdsRequest request
    ) {
        List<AvailableDateResponse> availableDates = availableDateService
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
