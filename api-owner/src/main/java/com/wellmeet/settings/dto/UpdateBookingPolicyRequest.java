package com.wellmeet.settings.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateBookingPolicyRequest {
    
    @Min(1) @Max(100)
    private Integer maxPartySize;
    
    @Min(1) @Max(100)
    private Integer minPartySize;
    
    @Min(0) @Max(365)
    private Integer advanceBookingDays;
    
    @Min(0) @Max(168) // 최대 1주일
    private Integer cancellationHours;
    
    private Boolean depositRequired;
    
    @Min(0)
    private BigDecimal depositAmount;
    
    private String depositPolicy;
    
    @Min(0)
    private BigDecimal noShowPenalty;
    
    private Boolean specialRequestsAllowed;
    
    @Min(15) @Max(120) // 15분 ~ 2시간
    private Integer bookingInterval;
    
    @Min(1) @Max(1000)
    private Integer maxBookingsPerDay;
    
    private List<String> blackoutDates;
}