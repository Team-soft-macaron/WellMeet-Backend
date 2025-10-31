package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AvailableDateResponse(
        Long id,
        LocalDate date,
        LocalTime time,
        int maxCapacity,
        boolean isAvailable,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AvailableDateResponse from(AvailableDate availableDate) {
        return new AvailableDateResponse(
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
