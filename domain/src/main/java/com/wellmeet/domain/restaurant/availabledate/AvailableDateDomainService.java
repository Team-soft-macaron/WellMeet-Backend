package com.wellmeet.domain.restaurant.availabledate;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
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

    public AvailableDate getByIdAndRestaurantId(Long id, String restaurantId) {
        return availableDateRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.AVAILABLE_DATE_NOT_FOUND));
    }
}
