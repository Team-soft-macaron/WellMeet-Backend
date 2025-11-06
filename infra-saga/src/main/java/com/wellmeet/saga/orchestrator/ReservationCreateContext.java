package com.wellmeet.saga.orchestrator;

public record ReservationCreateContext(
        String memberId,
        String restaurantId,
        Long availableDateId,
        int partySize,
        String specialRequest
) {}
