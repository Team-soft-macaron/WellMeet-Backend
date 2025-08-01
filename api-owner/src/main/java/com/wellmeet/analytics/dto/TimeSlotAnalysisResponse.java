package com.wellmeet.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotAnalysisResponse {

    private List<TimeSlotStats> timeSlots;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotStats {
        private String timeSlot;              // 시간대 (예: "09:00-10:00")
        private int hour;                     // 시간 (0-23)
        private int reservationCount;         // 예약 수
        private BigDecimal revenue;           // 매출
        private double occupancyRate;         // 점유율 (%)  
        private int averagePartySize;         // 평균 파티 크기
        private double averageWaitTime;       // 평균 대기 시간 (분)
        private List<DayOfWeekData> dayBreakdown; // 요일별 분석
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DayOfWeekData {
        private String dayOfWeek;             // 요일 (Monday, Tuesday, etc.)
        private int reservationCount;         // 예약 수
        private double occupancyRate;         // 점유율 (%)
    }
}