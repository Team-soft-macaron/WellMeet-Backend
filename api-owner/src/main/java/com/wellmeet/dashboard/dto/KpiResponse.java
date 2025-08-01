package com.wellmeet.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KpiResponse {
    
    private KpiMetric bookingCount;
    private KpiMetric revenue;
    private KpiMetric avgPartySize;
    private KpiMetric satisfactionScore;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiMetric {
        private double value;
        private double change;  // 전월 대비 변화율 (%)
    }
}