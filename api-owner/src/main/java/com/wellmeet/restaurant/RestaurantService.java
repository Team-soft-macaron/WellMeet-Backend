package com.wellmeet.restaurant;

import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantDomainService restaurantDomainService;

    public OperatingHoursResponse getOperatingHours() {
        return null;
    }
}
