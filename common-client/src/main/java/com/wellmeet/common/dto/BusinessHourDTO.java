package com.wellmeet.common.dto;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BusinessHourDTO(
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
}
