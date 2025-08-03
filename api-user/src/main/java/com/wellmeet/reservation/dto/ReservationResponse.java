package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDateTime;
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

    public ReservationResponse(Reservation reservation, double rating) {
        this.restaurantId = reservation.getRestaurant().getId();
        this.restaurantName = reservation.getRestaurant().getName();
        this.restaurantAddress = reservation.getRestaurant().getAddress();
        this.restaurantRating = rating;
        this.latitude = reservation.getRestaurant().getLatitude();
        this.longitude = reservation.getRestaurant().getLongitude();
        this.dateTime = reservation.getDateTime();
        this.partySize = reservation.getPartySize();
        this.purpose = reservation.getPurpose();
        this.specialRequest = reservation.getSpecialRequest();
        this.status = reservation.getStatus();
    }
}
