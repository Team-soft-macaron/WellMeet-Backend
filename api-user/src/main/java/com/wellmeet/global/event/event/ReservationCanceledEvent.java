package com.wellmeet.global.event.event;

import com.wellmeet.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ReservationCanceledEvent {

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

    public ReservationCanceledEvent(Reservation reservation, String memberName, String restaurantName, LocalDateTime dateTime) {
        this.reservationId = reservation.getId();
        this.memberId = reservation.getMemberId();
        this.memberName = memberName;
        this.restaurantId = reservation.getRestaurantId();
        this.restaurantName = restaurantName;
        this.status = reservation.getStatus().name();
        this.partySize = reservation.getPartySize();
        this.specialRequest = reservation.getSpecialRequest();
        this.dateTime = dateTime;
        this.createdAt = reservation.getCreatedAt();
    }
}
