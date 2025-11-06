package com.wellmeet.saga.orchestrator;

public record ReservationCreateContext(
        String memberId,
        String restaurantId,
        String availableDateId,
        int partySize,
        String specialRequest
) {}
