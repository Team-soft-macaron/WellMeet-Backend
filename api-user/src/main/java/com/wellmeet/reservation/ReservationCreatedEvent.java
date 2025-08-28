package com.wellmeet.reservation;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
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

    public ReservationCreatedEvent(
            Long reservationId,
            String memberId,
            String restaurantId,
            String restaurantName,
            String status,
            int partySize,
            String specialRequest,
            LocalDateTime dateTime,
            LocalDateTime createdAt
    ) {
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.status = status;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
        this.dateTime = dateTime;
        this.createdAt = createdAt;
    }
}
