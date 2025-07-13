package com.wellmeet.restaurant.repository;

import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.domain.RestaurantPremiumOption;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantPremiumOptionRepository extends JpaRepository<RestaurantPremiumOption, Long> {

    Optional<RestaurantPremiumOption> findByRestaurantAndPremiumOptionId(Restaurant restaurant, Long premiumOptionId);
}
