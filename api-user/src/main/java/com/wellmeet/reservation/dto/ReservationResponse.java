package com.wellmeet.reservation.dto;

import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@lombok.Builder
@lombok.AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private String restaurantId;
    private String restaurantName;
    private String restaurantAddress;
    private double restaurantRating;
    private double latitude;
    private double longitude;
    private LocalDateTime dateTime;
    private int partySize;
    private String specialRequest;
    private ReservationStatus status;

    public ReservationResponse(ReservationDTO reservation, RestaurantDTO restaurant, AvailableDateDTO availableDate, double rating) {
        this.id = reservation.getId();
        this.restaurantId = restaurant.getId();
        this.restaurantName = restaurant.getName();
        this.restaurantAddress = restaurant.getAddress();
        this.restaurantRating = rating;
        this.latitude = restaurant.getLatitude();
        this.longitude = restaurant.getLongitude();
        this.dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        this.partySize = reservation.getPartySize();
        this.specialRequest = reservation.getSpecialRequest();
        this.status = ReservationStatus.valueOf(reservation.getStatus());
    }
}
