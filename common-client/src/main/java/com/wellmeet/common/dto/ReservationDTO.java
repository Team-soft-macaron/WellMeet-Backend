package com.wellmeet.common.dto;

import java.time.LocalDateTime;

public record ReservationDTO(
        Long id,
        ReservationStatus status,
        String restaurantId,
        String memberId,
        Long availableDateId,
        int partySize,
        String specialRequest,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
