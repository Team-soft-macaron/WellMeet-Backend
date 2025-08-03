package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReservationResponse {

    private String restaurantName;
    private ReservationStatus status;
    private LocalDateTime dateTime;
    private int partySize;
    private String purpose;
    private String specialRequest;

    public CreateReservationResponse(Reservation reservation) {
        this.restaurantName = reservation.getRestaurant().getName();
        this.status = reservation.getStatus();
        this.dateTime = reservation.getDateTime();
        this.partySize = reservation.getPartySize();
        this.purpose = reservation.getPurpose();
        this.specialRequest = reservation.getSpecialRequest();
    }
}
