package com.wellmeet.restaurant.repository;

import com.wellmeet.restaurant.domain.PremiumOption;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PremiumOptionRepository extends JpaRepository<PremiumOption, Long> {

    Optional<PremiumOption> findByRestaurantAndId(Restaurant restaurant, Long id);
}
