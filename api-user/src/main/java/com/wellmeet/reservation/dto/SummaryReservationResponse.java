package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
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

    public SummaryReservationResponse(Reservation reservation, String restaurantName, AvailableDate availableDate) {
        this.id = reservation.getId();
        this.restaurantName = restaurantName;
        this.dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        this.partySize = reservation.getPartySize();
        this.status = reservation.getStatus();
    }
}
