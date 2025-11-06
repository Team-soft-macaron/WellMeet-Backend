package com.wellmeet.saga.orchestrator;

public record ReservationCancelContext(
        Long reservationId,
        String memberId,
        String restaurantId,
        String availableDateId,
        int partySize
) {}
