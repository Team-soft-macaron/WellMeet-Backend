package com.wellmeet.domain.businesshour.repository;

import com.wellmeet.domain.businesshour.entity.BusinessHour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessHourRepository extends JpaRepository<BusinessHour, Long> {

    List<BusinessHour> findAllByRestaurantId(String restaurantId);
}
