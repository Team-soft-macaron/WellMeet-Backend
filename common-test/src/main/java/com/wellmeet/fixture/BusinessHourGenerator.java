package com.wellmeet.fixture;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import com.wellmeet.domain.restaurant.businesshour.repository.BusinessHourRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class BusinessHourGenerator {

    private final BusinessHourRepository businessHourRepository;

    public BusinessHourGenerator(BusinessHourRepository businessHourRepository) {
        this.businessHourRepository = businessHourRepository;
    }

    public BusinessHour generate(DayOfWeek dayOfWeek, Restaurant restaurant) {
        BusinessHour businessHour = new BusinessHour(
                dayOfWeek,
                true,
                LocalTime.of(9, 0),
                LocalTime.of(21, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                restaurant
        );
        return businessHourRepository.save(businessHour);
    }
}
