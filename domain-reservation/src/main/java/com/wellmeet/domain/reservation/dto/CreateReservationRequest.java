package com.wellmeet.domain.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
        @NotBlank
        String memberId,

        @NotBlank
        String restaurantId,

        @NotNull
        Long availableDateId,

        @NotNull
        Integer partySize,

        String specialRequest
) {
}
