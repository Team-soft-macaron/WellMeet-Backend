package com.wellmeet.restaurant.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateOperatingHoursRequest {

    private DayHours monday;
    private DayHours tuesday;
    private DayHours wednesday;
    private DayHours thursday;
    private DayHours friday;
    private DayHours saturday;
    private DayHours sunday;
    private HolidayHours holidays;

    @Getter
    @NoArgsConstructor
    public static class DayHours {
        private String open;              // HH:mm
        private String close;             // HH:mm
        private Boolean isClosed;
        private BreakTime breakTime;
    }

    @Getter
    @NoArgsConstructor
    public static class BreakTime {
        private String start;
        private String end;
    }

    @Getter
    @NoArgsConstructor
    public static class HolidayHours {
        private Boolean isOpen;
        private HolidayTime hours;
    }

    @Getter
    @NoArgsConstructor
    public static class HolidayTime {
        private String open;
        private String close;
    }
}