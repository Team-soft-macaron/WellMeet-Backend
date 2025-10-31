package com.wellmeet.domain.restaurant.service;

import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.dto.AvailableDateResponse;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AvailableDateService {

    private final AvailableDateRepository availableDateRepository;

    public AvailableDateService(AvailableDateRepository availableDateRepository) {
        this.availableDateRepository = availableDateRepository;
    }

    public List<AvailableDateResponse> getAvailableDatesByRestaurantId(String restaurantId) {
        return availableDateRepository.findAllByRestaurantId(restaurantId)
                .stream()
                .map(AvailableDateResponse::from)
                .toList();
    }

    public List<AvailableDateResponse> getAvailableDatesByIds(List<Long> availableDateIds) {
        return availableDateRepository.findAllByIdIn(availableDateIds)
                .stream()
                .map(AvailableDateResponse::from)
                .toList();
    }

    @Transactional
    public void decreaseCapacity(Long availableDateId, int partySize) {
        int updatedCount = availableDateRepository.decreaseCapacity(availableDateId, partySize);

        if (updatedCount == 0) {
            throw new RestaurantException(RestaurantErrorCode.NOT_ENOUGH_CAPACITY);
        }
    }

    @Transactional
    public void increaseCapacity(Long availableDateId, int partySize) {
        availableDateRepository.increaseCapacity(availableDateId, partySize);
    }
}
