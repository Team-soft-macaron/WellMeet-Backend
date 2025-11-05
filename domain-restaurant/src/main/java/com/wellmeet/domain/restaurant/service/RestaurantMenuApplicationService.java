package com.wellmeet.domain.restaurant.service;

import com.wellmeet.domain.restaurant.dto.MenuResponse;
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

    public List<MenuResponse> getMenusByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(MenuResponse::from)
                .toList();
    }
}
