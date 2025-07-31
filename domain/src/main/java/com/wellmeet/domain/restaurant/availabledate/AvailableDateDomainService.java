package com.wellmeet.domain.restaurant.availabledate;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
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
}
