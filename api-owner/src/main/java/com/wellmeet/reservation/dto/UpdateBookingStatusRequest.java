package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBookingStatusRequest {
    
    @NotNull(message = "상태는 필수입니다.")
    private ReservationStatus status;
    
    private String reason;
}