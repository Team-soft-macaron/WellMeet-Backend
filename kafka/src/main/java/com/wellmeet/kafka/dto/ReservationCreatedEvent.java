package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationCreatedEvent {

    private final Long reservationId;
    private final String memberId;
    private final String restaurantId;
    private final String restaurantName;
    private final String status;
    private final int partySize;
    private final String specialRequest;
    private final LocalDateTime dateTime;
    private final LocalDateTime createdAt;
}
