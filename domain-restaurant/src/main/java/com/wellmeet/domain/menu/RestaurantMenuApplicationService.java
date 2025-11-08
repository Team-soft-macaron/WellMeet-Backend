package com.wellmeet.domain.menu;

import com.wellmeet.common.dto.MenuDTO;
import com.wellmeet.domain.menu.domainservice.MenuDomainService;
import com.wellmeet.domain.menu.entity.Menu;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantMenuApplicationService {

    private final MenuDomainService menuDomainService;

    public List<MenuDTO> getMenusByRestaurantId(String restaurantId) {
        return menuDomainService.getByRestaurantId(restaurantId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private MenuDTO toDTO(Menu menu) {
        return new MenuDTO(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.getRestaurant().getId(),
                menu.getCreatedAt(),
                menu.getUpdatedAt()
        );
    }
}
