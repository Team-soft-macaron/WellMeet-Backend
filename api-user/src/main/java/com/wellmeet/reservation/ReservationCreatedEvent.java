package com.wellmeet.reservation;

import java.time.LocalDateTime;

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

    public Long getReservationId() {
        return reservationId;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getStatus() {
        return status;
    }

    public int getPartySize() {
        return partySize;
    }

    public String getSpecialRequest() {
        return specialRequest;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
