package com.wellmeet.reservation.dto;

import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.ReservationDTO;
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
        this.id = reservation.getId();
        this.restaurantName = restaurantName;
        this.status = ReservationStatus.valueOf(reservation.getStatus());
        this.dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        this.partySize = reservation.getPartySize();
        this.specialRequest = reservation.getSpecialRequest();
    }
}
