package com.wellmeet.restaurant.dto;

import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOperatingHoursRequest {

    private List<DayHours> operatingHours;

    @Getter
    @NoArgsConstructor
    public static class DayHours {

        private DayOfWeek dayOfWeek;
        private boolean operating;
        private LocalTime open;
        private LocalTime close;
        private BreakTime breakTime;
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {

        private LocalTime start;
        private LocalTime end;
    }
}
