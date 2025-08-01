package com.wellmeet.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyAnalysisResponse {

    private List<WeeklyData> weeklyData;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WeeklyData {
        private String week;                  // 주차 (예: "2024-W01")
        private String startDate;             // 주 시작일 (YYYY-MM-DD)
        private String endDate;               // 주 종료일 (YYYY-MM-DD)
        private int reservationCount;         // 예약 수
        private BigDecimal revenue;           // 매출
        private int customerCount;            // 고객 수
        private double occupancyRate;         // 점유율 (%)
        private List<DailyData> dailyBreakdown; // 일별 분석
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyData {
        private String date;              // 날짜 (YYYY-MM-DD)
        private String dayOfWeek;         // 요일 (Monday, Tuesday, etc.)
        private int reservationCount;     // 예약 수
        private BigDecimal revenue;       // 매출
        private double occupancyRate;     // 점유율 (%)
    }
}