package com.wellmeet.domain.menu.repository;

import com.wellmeet.domain.menu.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findByRestaurantId(String restaurantId);
}
