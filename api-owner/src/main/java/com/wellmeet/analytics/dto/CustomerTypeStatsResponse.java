package com.wellmeet.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTypeStatsResponse {

    private List<CustomerTypeStats> customerTypes;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerTypeStats {
        private String type;                    // 고객 유형 (예: "new", "returning", "vip")
        private String displayName;             // 표시 이름 (예: "신규 고객", "재방문 고객", "VIP 고객")
        private int count;                      // 고객 수
        private double percentage;              // 비율 (%)
        private BigDecimal totalRevenue;        // 해당 유형 총 매출
        private BigDecimal averageOrderValue;   // 평균 주문 금액
        private int averageVisitCount;          // 평균 방문 횟수
        private CustomerBehavior behavior;      // 고객 행동 분석
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerBehavior {
        private String preferredTimeSlot;       // 선호 시간대
        private String preferredDayOfWeek;      // 선호 요일
        private int averagePartySize;           // 평균 파티 크기
        private String mostOrderedItem;         // 가장 많이 주문한 메뉴
        private double cancelationRate;         // 취소율 (%)
    }
}