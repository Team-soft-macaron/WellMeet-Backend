package com.wellmeet.reservation.dto;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.ReservationDTO;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@lombok.Builder
@lombok.AllArgsConstructor
public class CreateReservationResponse {

    private Long id;
    private String restaurantName;
    private ReservationStatus status;
    private LocalDateTime dateTime;
    private int partySize;
    private String specialRequest;

    public CreateReservationResponse(ReservationDTO reservation, String restaurantName, AvailableDateDTO availableDate) {
        this.id = reservation.id();
        this.restaurantName = restaurantName;
        this.status = ReservationStatus.valueOf(reservation.status().name());
        this.dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        this.partySize = reservation.partySize();
        this.specialRequest = reservation.specialRequest();
    }
}
