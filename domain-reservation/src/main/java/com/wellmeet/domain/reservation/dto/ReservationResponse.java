package com.wellmeet.domain.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String memberId,
        String restaurantId,
        Long availableDateId,
        Integer partySize,
        String specialRequest,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getMemberId(),
                reservation.getRestaurantId(),
                reservation.getAvailableDateId(),
                reservation.getPartySize(),
                reservation.getSpecialRequest(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}
