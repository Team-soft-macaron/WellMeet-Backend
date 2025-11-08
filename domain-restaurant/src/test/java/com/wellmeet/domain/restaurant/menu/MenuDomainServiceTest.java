package com.wellmeet.domain.restaurant.menu;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;

import com.wellmeet.domain.menu.domainservice.MenuDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.menu.entity.Menu;
import com.wellmeet.domain.menu.repository.MenuRepository;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(MenuDomainService.class)
class MenuDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private MenuDomainService menuDomainService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Nested
    class GetByRestaurantId {

        @Test
        void 식당의_메뉴를_조회한다() {
            Restaurant restaurant = createAndSaveRestaurant("restaurant");
            createAndSaveMenu("메뉴1", 10000, restaurant);
            createAndSaveMenu("메뉴2", 15000, restaurant);
            createAndSaveMenu("메뉴3", 20000, restaurant);

            List<Menu> result = menuDomainService.getByRestaurantId(restaurant.getId());

            assertThat(result).hasSize(3);
        }

        @Test
        void 메뉴가_없으면_빈_리스트를_반환한다() {
            Restaurant restaurant = createAndSaveRestaurant("restaurant");

            List<Menu> result = menuDomainService.getByRestaurantId(restaurant.getId());

            assertThat(result).isEmpty();
        }

        @Test
        void 다른_식당의_메뉴는_조회되지_않는다() {
            Restaurant restaurant1 = createAndSaveRestaurant("restaurant1");
            Restaurant restaurant2 = createAndSaveRestaurant("restaurant2");
            createAndSaveMenu("메뉴1", 10000, restaurant1);
            createAndSaveMenu("메뉴2", 15000, restaurant2);

            List<Menu> result = menuDomainService.getByRestaurantId(restaurant1.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("메뉴1");
        }
    }

    private Restaurant createAndSaveRestaurant(String name) {
        String ownerId = "test-owner-id";
        Restaurant restaurant = new Restaurant(name, "description", "address", 37.5, 127.0, "thumbnail", ownerId);
        return restaurantRepository.save(restaurant);
    }

    private Menu createAndSaveMenu(String name, int price, Restaurant restaurant) {
        Menu menu = new Menu(name, "description", price, restaurant);
        return menuRepository.save(menu);
    }
}
