package com.wellmeet.fixture;

import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.menu.repository.MenuRepository;
import org.springframework.stereotype.Component;

@Component
public class MenuGenerator {

    private final MenuRepository menuRepository;

    public MenuGenerator(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    public Menu generate(String name, int price, Restaurant restaurant) {
        Menu menu = new Menu(name, "description", price, restaurant);
        return menuRepository.save(menu);
    }
}
