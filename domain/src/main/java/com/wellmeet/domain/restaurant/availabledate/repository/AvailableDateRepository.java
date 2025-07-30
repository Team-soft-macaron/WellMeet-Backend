package com.wellmeet.domain.restaurant.availabledate.repository;

import com.wellmeet.domain.restaurant.availabledate.domain.AvailableDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {

    List<AvailableDate> findAllByRestaurantId(String restaurantId);
}
