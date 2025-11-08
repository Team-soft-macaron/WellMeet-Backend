package com.wellmeet.domain.availabledate.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AvailableDateIdsRequest(
        @NotEmpty
        List<Long> availableDateIds
) {
}
