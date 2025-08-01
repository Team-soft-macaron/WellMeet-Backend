package com.wellmeet.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodayStatsResponse {
    
    private int todayBookings;      // 오늘 전체 예약 수
    private int confirmedBookings;  // 확정된 예약 수
    private int pendingBookings;    // 대기 중인 예약 수
    private long expectedRevenue;   // 예상 매출 (원)
}