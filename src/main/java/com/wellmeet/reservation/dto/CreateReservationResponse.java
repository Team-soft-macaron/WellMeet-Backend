package com.wellmeet.reservation.dto;

import com.wellmeet.reservation.domain.Reservation;
import com.wellmeet.reservation.domain.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
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
    private List<String> selectedPremiumOptionNames;

    public CreateReservationResponse(Reservation reservation, List<String> options) {
        this.restaurantName = reservation.getRestaurant().getName();
        this.status = reservation.getStatus();
        this.dateTime = reservation.getDateTime();
        this.partySize = reservation.getPartySize();
        this.purpose = reservation.getPurpose();
        this.specialRequest = reservation.getSpecialRequest();
        this.selectedPremiumOptionNames = options;
    }
}
