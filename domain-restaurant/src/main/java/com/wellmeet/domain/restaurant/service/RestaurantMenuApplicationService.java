package com.wellmeet.domain.restaurant.service;

import com.wellmeet.common.dto.MenuDTO;
import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.menu.repository.MenuRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantMenuApplicationService {

    private final MenuRepository menuRepository;

    public RestaurantMenuApplicationService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public List<MenuDTO> getMenusByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId)
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
