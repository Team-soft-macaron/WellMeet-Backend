package com.wellmeet.domain.restaurant.controller;

import com.wellmeet.domain.restaurant.dto.MenuResponse;
import com.wellmeet.domain.restaurant.service.RestaurantMenuApplicationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
public class RestaurantMenuController {

    private final RestaurantMenuApplicationService menuService;

    public RestaurantMenuController(RestaurantMenuApplicationService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuResponse>> getMenusByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<MenuResponse> menus = menuService.getMenusByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }
}
