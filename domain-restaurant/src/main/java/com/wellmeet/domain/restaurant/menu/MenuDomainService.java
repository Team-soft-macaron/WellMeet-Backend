package com.wellmeet.domain.restaurant.menu;

import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.menu.repository.MenuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuDomainService {

    private final MenuRepository menuRepository;

    public List<Menu> getByRestaurantId(String restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId);
    }
}
