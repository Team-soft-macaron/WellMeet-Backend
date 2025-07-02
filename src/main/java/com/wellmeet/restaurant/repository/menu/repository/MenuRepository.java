package com.wellmeet.restaurant.repository.menu.repository;

import com.wellmeet.restaurant.domain.menu.domain.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByRestaurantId(Long restaurantId);
}
