package com.wellmeet.domain.restaurant.availabledate.repository;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {

    List<AvailableDate> findAllByRestaurantId(String restaurantId);

    Optional<AvailableDate> findByIdAndRestaurant(Long id, Restaurant restaurant);
}
