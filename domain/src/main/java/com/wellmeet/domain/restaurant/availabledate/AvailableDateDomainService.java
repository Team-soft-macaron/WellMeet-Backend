package com.wellmeet.domain.restaurant.availabledate;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailableDateDomainService {

    private final AvailableDateRepository availableDateRepository;

    public List<AvailableDate> getAvailableDatesByRestaurantId(String restaurantId) {
        return availableDateRepository.findAllByRestaurantId(restaurantId);
    }

    public AvailableDate getByIdAndRestaurant(Long id, Restaurant restaurant) {
        return availableDateRepository.findByIdAndRestaurant(id, restaurant)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.AVAILABLE_DATE_NOT_FOUND));
    }
}
