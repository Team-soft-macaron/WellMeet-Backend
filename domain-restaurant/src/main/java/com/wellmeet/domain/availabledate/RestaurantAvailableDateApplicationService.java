package com.wellmeet.domain.availabledate;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.domain.availabledate.domainservice.AvailableDateDomainService;
import com.wellmeet.domain.availabledate.entity.AvailableDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantAvailableDateApplicationService {

    private final AvailableDateDomainService availableDateDomainService;

    public List<AvailableDateDTO> getAvailableDatesByRestaurantId(String restaurantId) {
        return availableDateDomainService.getAvailableDatesByRestaurantId(restaurantId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<AvailableDateDTO> getAvailableDatesByIds(List<Long> availableDateIds) {
        return availableDateDomainService.findAllByIds(availableDateIds)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public void decreaseCapacity(Long availableDateId, int partySize) {
        availableDateDomainService.decreaseCapacity(availableDateId, partySize);
    }

    @Transactional
    public void increaseCapacity(Long availableDateId, int partySize) {
        availableDateDomainService.increaseCapacity(availableDateId, partySize);
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
