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
public class SummaryReservationResponse {

    private Long id;
    private String restaurantName;
    private LocalDateTime dateTime;
    private int partySize;
    private ReservationStatus status;

    public SummaryReservationResponse(ReservationDTO reservation, String restaurantName, AvailableDateDTO availableDate) {
        this.id = reservation.id();
        this.restaurantName = restaurantName;
        this.dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        this.partySize = reservation.partySize();
        this.status = ReservationStatus.valueOf(reservation.status().name());
    }
}
