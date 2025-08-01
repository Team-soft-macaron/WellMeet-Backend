package com.wellmeet.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAnalysisResponse {

    private String period;                    // 분석 기간 (daily, weekly, monthly)
    private BigDecimal totalRevenue;          // 총 매출
    private BigDecimal averageRevenue;        // 평균 매출
    private double growthRate;                // 성장률 (%)
    private List<RevenueData> revenueData;    // 매출 데이터

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueData {
        private String date;                  // 날짜/기간 (YYYY-MM-DD 또는 YYYY-MM 또는 YYYY-Www)
        private String displayName;           // 표시 이름 (예: "1월", "1주차")
        private BigDecimal revenue;           // 매출
        private int reservationCount;         // 예약 수
        private BigDecimal averageOrderValue; // 평균 주문 금액
        private double growthFromPrevious;    // 이전 기간 대비 성장률 (%)
        private RevenueBreakdown breakdown;   // 매출 세부 분석
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueBreakdown {
        private BigDecimal foodRevenue;       // 음식 매출
        private BigDecimal beverageRevenue;   // 음료 매출
        private BigDecimal serviceRevenue;    // 서비스 매출 (예: 서비스 수수료)
        private BigDecimal discountAmount;    // 할인 금액
        private BigDecimal taxAmount;         // 세금
    }
}