package com.wellmeet.domain.restaurant.service;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import com.wellmeet.domain.restaurant.businesshour.repository.BusinessHourRepository;
import com.wellmeet.domain.restaurant.dto.BusinessHoursResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantBusinessHourApplicationService {

    private final BusinessHourRepository businessHourRepository;

    public RestaurantBusinessHourApplicationService(BusinessHourRepository businessHourRepository) {
        this.businessHourRepository = businessHourRepository;
    }

    public BusinessHoursResponse getBusinessHoursByRestaurantId(String restaurantId) {
        BusinessHours businessHours = new BusinessHours(
                businessHourRepository.findAllByRestaurantId(restaurantId)
        );

        return BusinessHoursResponse.from(businessHours);
    }
}
