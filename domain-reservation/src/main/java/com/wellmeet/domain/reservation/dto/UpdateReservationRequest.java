package com.wellmeet.domain.reservation.dto;

import com.wellmeet.domain.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateReservationRequest(
        @NotNull
        Integer partySize,

        String specialRequest,

        @NotNull
        ReservationStatus status
) {
}
