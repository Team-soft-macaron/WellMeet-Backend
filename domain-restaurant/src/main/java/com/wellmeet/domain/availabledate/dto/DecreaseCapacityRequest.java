package com.wellmeet.domain.availabledate.dto;

import jakarta.validation.constraints.NotNull;

public record DecreaseCapacityRequest(
        @NotNull
        Long availableDateId,

        @NotNull
        Integer partySize
) {
}
