package com.wellmeet.domain.availabledate.dto;

import jakarta.validation.constraints.NotNull;

public record IncreaseCapacityRequest(
        @NotNull
        Long availableDateId,

        @NotNull
        Integer partySize
) {
}
