package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.NotNull;

public record IncreaseCapacityRequest(
        @NotNull
        Long availableDateId,

        @NotNull
        Integer partySize
) {
}
