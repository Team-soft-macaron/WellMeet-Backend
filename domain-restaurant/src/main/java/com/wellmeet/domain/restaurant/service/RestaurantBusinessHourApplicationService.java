package com.wellmeet.domain.restaurant.service;

import com.wellmeet.common.dto.BusinessHourDTO;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.repository.BusinessHourRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantBusinessHourApplicationService {

    private final BusinessHourRepository businessHourRepository;

    public RestaurantBusinessHourApplicationService(BusinessHourRepository businessHourRepository) {
        this.businessHourRepository = businessHourRepository;
    }

    public List<BusinessHourDTO> getBusinessHoursByRestaurantId(String restaurantId) {
        return businessHourRepository.findAllByRestaurantId(restaurantId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private BusinessHourDTO toDTO(BusinessHour businessHour) {
        return new BusinessHourDTO(
                businessHour.getId(),
                convertDayOfWeek(businessHour.getDayOfWeek()),
                businessHour.isOpen(),
                businessHour.getOpenTime(),
                businessHour.getCloseTime(),
                businessHour.getBreakStartTime(),
                businessHour.getBreakEndTime(),
                businessHour.getRestaurant().getId(),
                businessHour.getCreatedAt(),
                businessHour.getUpdatedAt()
        );
    }

    private java.time.DayOfWeek convertDayOfWeek(com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> java.time.DayOfWeek.MONDAY;
            case TUESDAY -> java.time.DayOfWeek.TUESDAY;
            case WEDNESDAY -> java.time.DayOfWeek.WEDNESDAY;
            case THURSDAY -> java.time.DayOfWeek.THURSDAY;
            case FRIDAY -> java.time.DayOfWeek.FRIDAY;
            case SATURDAY -> java.time.DayOfWeek.SATURDAY;
            case SUNDAY -> java.time.DayOfWeek.SUNDAY;
            case HOLIDAY -> java.time.DayOfWeek.SUNDAY;
        };
    }
}
