package com.wellmeet.customer.dto;

import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerBookingHistoryResponse {

    private List<BookingHistory> bookings;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingHistory {
        private Long id;
        private String date;           // YYYY-MM-DD
        private String time;           // HH:mm
        private int party;
        private ReservationStatus status;
        private Integer tableNumber;
        private BigDecimal totalAmount;
        private LocalDateTime createdAt;
    }
}