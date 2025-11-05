package com.wellmeet.common.dto.request;

public record CreateReservationDTO(
        String restaurantId,
        Long availableDateId,
        String memberId,
        int partySize,
        String specialRequest
) {
}
