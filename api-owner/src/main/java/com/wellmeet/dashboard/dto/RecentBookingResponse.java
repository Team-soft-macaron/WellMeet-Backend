package com.wellmeet.dashboard.dto;

import com.wellmeet.domain.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecentBookingResponse {
    
    private Long id;
    private CustomerInfo customer;
    private String time;  // ISO 8601
    private int party;
    private ReservationStatus status;
    private String special;
    private Integer tableNumber;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Long id;
        private String name;
        private String phone;
        private boolean isVip;
    }
}