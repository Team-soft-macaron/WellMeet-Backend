package com.wellmeet.saga.orchestrator;

public record ReservationUpdateContext(
        Long reservationId,
        String memberId,
        String oldRestaurantId,
        String oldAvailableDateId,
        int oldPartySize,
        String newRestaurantId,
        String newAvailableDateId,
        int newPartySize,
        String specialRequest
) {}
