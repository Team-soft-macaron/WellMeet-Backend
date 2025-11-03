package com.wellmeet.reservation.dto;

import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
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
    private ReservationStatus status;

    public SummaryReservationResponse(ReservationDTO reservation, String restaurantName, AvailableDateDTO availableDate) {
        this.id = reservation.getId();
        this.restaurantName = restaurantName;
        this.dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        this.partySize = reservation.getPartySize();
        this.status = ReservationStatus.valueOf(reservation.getStatus());
    }
}
