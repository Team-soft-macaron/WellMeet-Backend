package com.wellmeet.domain.restaurant.repository;

import com.wellmeet.domain.restaurant.entity.PremiumOption;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumOptionRepository extends JpaRepository<PremiumOption, Long> {

    Optional<PremiumOption> findByRestaurantAndId(Restaurant restaurant, Long id);
}
