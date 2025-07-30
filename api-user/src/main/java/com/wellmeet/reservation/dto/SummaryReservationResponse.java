package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.domain.Reservation;
import com.wellmeet.domain.reservation.domain.ReservationStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SummaryReservationResponse {

    private Long id;
    private String restaurantName;
    private LocalDateTime dateTime;
    private int partySize;
    private String purpose;
    private ReservationStatus status;

    public SummaryReservationResponse(Reservation reservation) {
        this.id = reservation.getId();
        this.restaurantName = reservation.getRestaurantName();
        this.dateTime = reservation.getReservationDateTime();
        this.partySize = reservation.getPartySize();
        this.purpose = reservation.getPurpose();
        this.status = reservation.getStatus();
    }
}
