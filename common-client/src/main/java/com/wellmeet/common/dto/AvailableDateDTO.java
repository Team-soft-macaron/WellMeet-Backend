package com.wellmeet.common.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record AvailableDateDTO(
        Long id,
        LocalDate date,
        LocalTime time,
        int maxCapacity,
        boolean isAvailable,
        String restaurantId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
