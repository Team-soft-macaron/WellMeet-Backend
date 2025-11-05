package com.wellmeet.reservation.dto;

import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.restaurant.dto.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {

    private Long id;
    private CustomerSummaryResponse customer;
    private LocalDate date;
    private LocalTime time;
    private int party;
    private ReservationStatus status;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservationResponse(ReservationDTO reservation, AvailableDateDTO availableDate, String memberName,
                               String memberPhone, String memberEmail, boolean memberVip) {
        CustomerSummaryResponse customerResponse = new CustomerSummaryResponse(
                reservation.memberId(),
                memberName,
                memberPhone,
                memberEmail,
                memberVip
        );

        this.id = reservation.id();
        this.customer = customerResponse;
        this.date = availableDate.date();
        this.time = availableDate.time();
        this.party = reservation.partySize();
        this.status = ReservationStatus.valueOf(reservation.status().name());
        this.note = reservation.specialRequest();
        this.createdAt = reservation.createdAt();
        this.updatedAt = reservation.updatedAt();
    }

    @Getter
    @NoArgsConstructor
    public static class CustomerSummaryResponse {

        private String id;
        private String name;
        private String phone;
        private String email;
        private boolean vip;

        public CustomerSummaryResponse(String id, String name, String phone, String email, boolean vip) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.vip = vip;
        }
    }
}
