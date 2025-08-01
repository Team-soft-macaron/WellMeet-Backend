package com.wellmeet.menu;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.menu.dto.CreateMenuRequest;
import com.wellmeet.menu.dto.MenuListResponse;
import com.wellmeet.menu.dto.MenuResponse;
import com.wellmeet.menu.dto.UpdateMenuAvailabilityRequest;
import com.wellmeet.menu.dto.UpdateMenuRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/restaurant/menu")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public MenuListResponse getMenus(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return menuService.getMenus(ownerId);
    }

    @PostMapping
    public MenuResponse createMenu(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody CreateMenuRequest request
    ) {
        return menuService.createMenu(ownerId, request);
    }

    @PatchMapping("/{id}")
    public MenuResponse updateMenu(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request
    ) {
        return menuService.updateMenu(ownerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenu(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id
    ) {
        menuService.deleteMenu(ownerId, id);
    }

    @PatchMapping("/{id}/availability")
    public MenuResponse updateMenuAvailability(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuAvailabilityRequest request
    ) {
        return menuService.updateMenuAvailability(ownerId, id, request);
    }
}
