package com.wellmeet.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull
    Long restaurantId;

    @NotNull
    LocalDateTime dateTime;

    @NotBlank
    String purpose;

    @NotNull
    List<Long> selectedPremiumOptionIds;

    int partySize;
    String specialRequest;
}
