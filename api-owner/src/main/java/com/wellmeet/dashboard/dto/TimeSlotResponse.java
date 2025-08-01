package com.wellmeet.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotResponse {
    
    private String time;         // 시간 (HH:mm)
    private int reservations;    // 예약 수
    private int capacity;        // 최대 수용 가능 수
    private boolean available;   // 예약 가능 여부
}