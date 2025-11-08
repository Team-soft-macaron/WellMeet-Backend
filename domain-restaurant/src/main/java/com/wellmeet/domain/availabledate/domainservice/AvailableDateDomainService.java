package com.wellmeet.domain.availabledate.domainservice;

import com.wellmeet.domain.availabledate.entity.AvailableDate;
import com.wellmeet.domain.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
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

    public void decreaseCapacity(AvailableDate availableDate, int partySize) {
        int row = availableDateRepository.decreaseCapacity(availableDate.getId(), partySize);
        if (row == 0) {
            throw new RestaurantException(RestaurantErrorCode.NOT_ENOUGH_CAPACITY);
        }
    }

    public void increaseCapacity(AvailableDate availableDate, int partySize) {
        availableDateRepository.increaseCapacity(availableDate.getId(), partySize);
    }

    public List<AvailableDate> findAllByIds(List<Long> availableDateIds) {
        return availableDateRepository.findAllByIdIn(availableDateIds);
    }
}
