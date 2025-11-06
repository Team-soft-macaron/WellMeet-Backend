package com.wellmeet.reservation.dto;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
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
        this.id = reservation.id();
        this.restaurantId = restaurant.id();
        this.restaurantName = restaurant.name();
        this.restaurantAddress = restaurant.address();
        this.restaurantRating = rating;
        this.latitude = restaurant.latitude();
        this.longitude = restaurant.longitude();
        this.dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        this.partySize = reservation.partySize();
        this.specialRequest = reservation.specialRequest();
        this.status = ReservationStatus.valueOf(reservation.status().name());
    }
}
