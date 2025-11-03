package com.wellmeet.restaurant.dto;

import com.wellmeet.reservation.dto.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOperatingHoursRequest {

    private List<DayHours> operatingHours;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayHours {

        private DayOfWeek dayOfWeek;
        private boolean operating;
        private LocalTime open;
        private LocalTime close;
        private LocalTime breakStart;
        private LocalTime breakEnd;
    }
}
