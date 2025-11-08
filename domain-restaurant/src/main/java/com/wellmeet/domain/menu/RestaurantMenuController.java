package com.wellmeet.domain.menu;

import com.wellmeet.common.dto.MenuDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class RestaurantMenuController {

    private final RestaurantMenuApplicationService menuService;

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuDTO>> getMenusByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<MenuDTO> menus = menuService.getMenusByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }
}
