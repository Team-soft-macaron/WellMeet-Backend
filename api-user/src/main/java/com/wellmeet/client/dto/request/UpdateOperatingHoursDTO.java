package com.wellmeet.client.dto.request;

import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOperatingHoursDTO {

    private List<DayHoursDTO> operatingHours;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayHoursDTO {
        private String dayOfWeek;
        private boolean isOperating;
        private LocalTime open;
        private LocalTime close;
        private LocalTime breakStart;
        private LocalTime breakEnd;
    }
}