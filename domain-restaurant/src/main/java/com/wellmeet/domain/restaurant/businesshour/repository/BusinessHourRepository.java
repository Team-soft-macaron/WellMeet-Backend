package com.wellmeet.domain.restaurant.businesshour.repository;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

    List<BusinessHour> findAllByRestaurantId(String restaurantId);
}
