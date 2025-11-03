package com.wellmeet.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReservationDTO {

    private String restaurantId;
    private Long availableDateId;
    private int partySize;
    private String specialRequest;
}
