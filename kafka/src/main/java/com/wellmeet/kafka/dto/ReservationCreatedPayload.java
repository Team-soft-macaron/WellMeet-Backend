package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationCreatedPayload {
    private final Long reservationId;
    private final String restaurantName;
    private final String customerName;
    private final LocalDateTime reservationTime;
    private final int partySize;
}
