package com.wellmeet.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    private Stats stats;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private int totalReservations;        // 총 예약 수
        private int confirmedReservations;    // 확정된 예약 수
        private int completedReservations;    // 완료된 예약 수
        private int cancelledReservations;    // 취소된 예약 수
        private BigDecimal totalRevenue;      // 총 매출
        private BigDecimal averageOrderValue; // 평균 주문 금액
        private int totalCustomers;           // 총 고객 수
        private int newCustomers;             // 신규 고객 수
        private int returningCustomers;       // 재방문 고객 수
        private double occupancyRate;         // 점유율 (%)
        private List<RecentActivity> recentActivities; // 최근 활동
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String type;        // 활동 유형 (예: "reservation", "cancellation")
        private String description; // 설명
        private String timestamp;   // 시간 (ISO 8601)
    }
}