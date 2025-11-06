package com.wellmeet.common.dto.request;

import java.time.LocalTime;
import java.util.List;

public record UpdateOperatingHoursDTO(
        List<DayHoursDTO> operatingHours
) {
    public record DayHoursDTO(
            String dayOfWeek,
            boolean isOperating,
            LocalTime open,
            LocalTime close,
            LocalTime breakStart,
            LocalTime breakEnd
    ) {
    }
}
