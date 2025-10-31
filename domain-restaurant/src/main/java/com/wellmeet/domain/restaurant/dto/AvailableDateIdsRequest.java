package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AvailableDateIdsRequest(
        @NotEmpty(message = "Available date IDs는 비어있을 수 없습니다")
        List<Long> availableDateIds
) {
}
