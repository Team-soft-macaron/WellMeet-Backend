package com.wellmeet.domain.reservation.dto;

import com.wellmeet.domain.reservation.entity.ReservationStatus;

public record UpdateReservationRequest(
        Integer partySize,

        String specialRequest,

        ReservationStatus status
) {
}
