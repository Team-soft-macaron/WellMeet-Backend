package com.wellmeet.global.event.event;

import com.wellmeet.common.dto.ReservationDTO;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReservationUpdatedEvent {

    private final Long reservationId;
    private final String memberId;
    private final String memberName;
    private final String restaurantId;
    private final String restaurantName;
    private final String status;
    private final int partySize;
    private final String specialRequest;
    private final LocalDateTime dateTime;
    private final LocalDateTime createdAt;

    public ReservationUpdatedEvent(ReservationDTO reservation, String memberName, String restaurantName, LocalDateTime dateTime) {
        this.reservationId = reservation.id();
        this.memberId = reservation.memberId();
        this.memberName = memberName;
        this.restaurantId = reservation.restaurantId();
        this.restaurantName = restaurantName;
        this.status = reservation.status().name();
        this.partySize = reservation.partySize();
        this.specialRequest = reservation.specialRequest();
        this.dateTime = dateTime;
        this.createdAt = reservation.createdAt();
    }
}
