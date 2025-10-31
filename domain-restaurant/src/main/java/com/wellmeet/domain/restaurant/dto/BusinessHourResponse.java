package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BusinessHourResponse(
        Long id,
        DayOfWeek dayOfWeek,
        boolean isOpen,
        LocalTime openTime,
        LocalTime closeTime,
        LocalTime breakStartTime,
        LocalTime breakEndTime,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BusinessHourResponse from(BusinessHour businessHour) {
        return new BusinessHourResponse(
                businessHour.getId(),
                businessHour.getDayOfWeek(),
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
}
