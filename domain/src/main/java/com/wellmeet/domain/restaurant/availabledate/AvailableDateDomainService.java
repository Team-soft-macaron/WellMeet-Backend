package com.wellmeet.domain.restaurant.availabledate;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
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
                .orElseThrow(() -> new WellMeetDomainException(DomainErrorCode.AVAILABLE_DATE_NOT_FOUND));
    }
}
