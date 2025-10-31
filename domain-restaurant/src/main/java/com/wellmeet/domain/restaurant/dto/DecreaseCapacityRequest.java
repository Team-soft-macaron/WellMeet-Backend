package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DecreaseCapacityRequest(
        @NotNull(message = "Available date ID는 필수입니다")
        Long availableDateId,

        @NotNull(message = "Party size는 필수입니다")
        @Min(value = 1, message = "Party size는 최소 1명 이상이어야 합니다")
        Integer partySize
) {
}
