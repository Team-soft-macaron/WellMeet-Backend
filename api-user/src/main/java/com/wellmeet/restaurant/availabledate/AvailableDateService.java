package com.wellmeet.restaurant.availabledate;

import com.wellmeet.restaurant.availabledate.domain.AvailableDate;
import com.wellmeet.restaurant.availabledate.repository.AvailableDateRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvailableDateService {

    private final AvailableDateRepository availableDateRepository;

    public List<AvailableDate> findAvailableDates(String restaurantId) {
        return availableDateRepository.findAllByRestaurantId(restaurantId);
    }
}
