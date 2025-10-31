package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.NotNull;

public record DecreaseCapacityRequest(
        @NotNull
        Long availableDateId,

        @NotNull
        Integer partySize
) {
}
