package com.wellmeet.restaurant.model.menu.repository;

import com.wellmeet.restaurant.model.menu.domain.Menu;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByRestaurantId(UUID restaurantId);
}
