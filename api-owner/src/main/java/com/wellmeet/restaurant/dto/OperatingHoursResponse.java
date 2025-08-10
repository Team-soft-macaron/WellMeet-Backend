package com.wellmeet.restaurant.dto;

import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OperatingHoursResponse {

    private OperatingHours operatingHours;

    @Getter
    @NoArgsConstructor
    public static class OperatingHours {
        private DayHours monday;
        private DayHours tuesday;
        private DayHours wednesday;
        private DayHours thursday;
        private DayHours friday;
        private DayHours saturday;
        private DayHours sunday;
        private HolidayHours holidays;
    }

    @Getter
    @NoArgsConstructor
    public static class DayHours {
        private LocalTime open;
        private LocalTime close;
        private boolean isClosed;
        private BreakTime breakTime;
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {
        private LocalTime start;
        private LocalTime end;
    }

    @Getter
    @NoArgsConstructor
    public static class HolidayHours {
        private boolean isOpen;
        private HolidayTime hours;
    }

    @Getter
    @NoArgsConstructor
    public static class HolidayTime {
        private LocalTime open;
        private LocalTime close;
    }
}
