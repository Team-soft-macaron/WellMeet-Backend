package com.wellmeet.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPolicyResponse {
    
    private BookingPolicy policy;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingPolicy {
        private int maxPartySize;           // 최대 예약 인원
        private int minPartySize;           // 최소 예약 인원
        private int advanceBookingDays;     // 사전 예약 가능 일수
        private int cancellationHours;      // 취소 가능 시간 (예약 시간 기준)
        private boolean depositRequired;    // 예약금 필요 여부
        private BigDecimal depositAmount;   // 예약금 액수
        private String depositPolicy;       // 예약금 정책 설명
        private BigDecimal noShowPenalty;   // 노쇼 패널티 금액
        private boolean specialRequestsAllowed; // 특별 요청 허용 여부
        private int bookingInterval;        // 예약 시간 간격 (분)
        private int maxBookingsPerDay;      // 일일 최대 예약 수
        private List<String> blackoutDates; // 예약 불가 날짜 (YYYY-MM-DD)
    }
}