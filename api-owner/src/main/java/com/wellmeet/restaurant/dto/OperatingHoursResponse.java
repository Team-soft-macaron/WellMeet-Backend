package com.wellmeet.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursResponse {

    private OperatingHours operatingHours;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OperatingHours {
        private DayHours monday;
        private DayHours tuesday;
        private DayHours wednesday;
        private DayHours thursday;
        private DayHours friday;
        private DayHours saturday;
        private DayHours sunday;
        private HolidayHours holidays;      // 공휴일 운영
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayHours {
        private String open;              // 오픈 시간 (HH:mm)
        private String close;             // 마감 시간 (HH:mm)
        private boolean isClosed;         // 휴무일 여부
        private BreakTime breakTime;      // 브레이크타임
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BreakTime {
        private String start;
        private String end;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HolidayHours {
        private boolean isOpen;           // 공휴일 영업 여부
        private HolidayTime hours;        // 공휴일 운영시간
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HolidayTime {
        private String open;
        private String close;
    }
}