package com.wellmeet.domain.fixture;

import com.wellmeet.domain.availabledate.entity.AvailableDate;
import com.wellmeet.domain.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class AvailableDateGenerator {

    private final AvailableDateRepository availableDateRepository;

    public AvailableDateGenerator(AvailableDateRepository availableDateRepository) {
        this.availableDateRepository = availableDateRepository;
    }

    public AvailableDate generate(LocalDateTime dateTime, int capacity, Restaurant restaurant) {
        AvailableDate availableDate = new AvailableDate(dateTime.toLocalDate(), dateTime.toLocalTime(), capacity,
                restaurant);
        return availableDateRepository.save(availableDate);
    }
}
