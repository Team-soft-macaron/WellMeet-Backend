package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.domain.Reservation;
import com.wellmeet.domain.reservation.domain.ReservationStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationResponse {

    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private double restaurantRating;
    private double latitude;
    private double longitude;
    private LocalDateTime dateTime;
    private int partySize;
    private String purpose;
    private String specialRequest;
    private ReservationStatus status;
    private List<String> selectedOptionNames;

    public ReservationResponse(Reservation reservation, double rating, List<String> selectedOptions) {
        this.restaurantId = reservation.getRestaurant().getId();
        this.restaurantName = reservation.getRestaurant().getName();
        this.restaurantAddress = reservation.getRestaurant().getAddress();
        this.restaurantRating = rating;
        this.latitude = reservation.getRestaurant().getLatitude();
        this.longitude = reservation.getRestaurant().getLongitude();
        this.dateTime = reservation.getReservationDateTime();
        this.partySize = reservation.getPartySize();
        this.purpose = reservation.getPurpose();
        this.specialRequest = reservation.getSpecialRequest();
        this.status = reservation.getStatus();
        this.selectedOptionNames = selectedOptions;
    }
}
