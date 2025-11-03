package com.wellmeet.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationDTO {

    private String restaurantId;
    private Long availableDateId;
    private String memberId;
    private int partySize;
    private String specialRequest;
}
