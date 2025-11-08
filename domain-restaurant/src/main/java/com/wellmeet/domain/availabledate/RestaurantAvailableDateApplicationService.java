package com.wellmeet.domain.availabledate;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.domain.availabledate.entity.AvailableDate;
import com.wellmeet.domain.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantAvailableDateApplicationService {

    private final AvailableDateRepository availableDateRepository;

    public List<AvailableDateDTO> getAvailableDatesByRestaurantId(String restaurantId) {
        return availableDateRepository.findAllByRestaurantId(restaurantId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<AvailableDateDTO> getAvailableDatesByIds(List<Long> availableDateIds) {
        return availableDateRepository.findAllByIdIn(availableDateIds)
                .stream()
                .map(this::toDTO)
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

    private AvailableDateDTO toDTO(AvailableDate availableDate) {
        return new AvailableDateDTO(
                availableDate.getId(),
                availableDate.getDate(),
                availableDate.getTime(),
                availableDate.getMaxCapacity(),
                availableDate.isAvailable(),
                availableDate.getRestaurant().getId(),
                availableDate.getCreatedAt(),
                availableDate.getUpdatedAt()
        );
    }
}
