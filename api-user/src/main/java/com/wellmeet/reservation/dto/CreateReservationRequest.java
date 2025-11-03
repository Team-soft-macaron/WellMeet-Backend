package com.wellmeet.reservation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull
    String restaurantId;

    @NotNull
    Long availableDateId;

    int partySize;
    String specialRequest;

    public CreateReservationRequest(String restaurantId, Long availableDateId, int partySize, String specialRequest) {
        this.restaurantId = restaurantId;
        this.availableDateId = availableDateId;
        this.partySize = partySize;
        this.specialRequest = specialRequest;
    }
}
