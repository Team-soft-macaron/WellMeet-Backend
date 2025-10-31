package com.wellmeet.domain.restaurant;

import com.wellmeet.domain.restaurant.dto.RestaurantIdsRequest;
import com.wellmeet.domain.restaurant.dto.RestaurantResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;

    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable String id) {
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        List<RestaurantResponse> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByIds(
            @Valid @RequestBody RestaurantIdsRequest request
    ) {
        List<RestaurantResponse> restaurants = restaurantService.getRestaurantsByIds(request.restaurantIds());
        return ResponseEntity.ok(restaurants);
    }
}
