package com.wellmeet.global.event.event;

import com.wellmeet.domain.reservation.entity.Reservation;
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

    public ReservationUpdatedEvent(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.memberId = reservation.getMember().getId();
        this.memberName = reservation.getMember().getName();
        this.restaurantId = reservation.getRestaurant().getId();
        this.restaurantName = reservation.getRestaurantName();
        this.status = reservation.getStatus().name();
        this.partySize = reservation.getPartySize();
        this.specialRequest = reservation.getSpecialRequest();
        this.dateTime = reservation.getDateTime();
        this.createdAt = reservation.getCreatedAt();
    }
}
