package com.wellmeet.saga.orchestrator;

public record ReservationUpdateContext(
        Long reservationId,
        String memberId,
        String oldRestaurantId,
        Long oldAvailableDateId,
        int oldPartySize,
        String newRestaurantId,
        Long newAvailableDateId,
        int newPartySize,
        String specialRequest
) {}
