package com.wellmeet.restaurant;

import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantDomainService restaurantDomainService;

    public OperatingHoursResponse getOperatingHours(String restaurantId) {
        List<BusinessHour> operatingHours = restaurantDomainService.getOperatingHours(restaurantId);
        return new OperatingHoursResponse(operatingHours);
    }
}
