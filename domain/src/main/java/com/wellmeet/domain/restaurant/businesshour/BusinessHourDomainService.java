package com.wellmeet.domain.restaurant.businesshour;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import com.wellmeet.domain.restaurant.businesshour.repository.BusinessHourRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BusinessHourDomainService {

    private final BusinessHourRepository businessHourRepository;

    public BusinessHours getOperatingHours(String restaurantId) {
        List<BusinessHour> hours = businessHourRepository.findAllByRestaurantId(restaurantId);
        return new BusinessHours(hours);
    }
}
