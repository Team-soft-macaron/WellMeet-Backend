package com.wellmeet.domain.restaurant;

import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.domain.restaurant.dto.RestaurantIdsRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantDomainController {

    private final RestaurantApplicationService restaurantApplicationService;

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable String id) {
        RestaurantDTO response = restaurantApplicationService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants() {
        List<RestaurantDTO> restaurants = restaurantApplicationService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<RestaurantDTO>> getRestaurantsByIds(
            @Valid @RequestBody RestaurantIdsRequest request
    ) {
        List<RestaurantDTO> restaurants = restaurantApplicationService.getRestaurantsByIds(request.restaurantIds());
        return ResponseEntity.ok(restaurants);
    }
}
