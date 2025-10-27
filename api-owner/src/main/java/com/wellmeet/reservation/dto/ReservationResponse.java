package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
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

    public ReservationResponse(Reservation reservation, String memberName, String memberPhone, String memberEmail, boolean memberVip) {
        CustomerSummaryResponse customerResponse = new CustomerSummaryResponse(
                reservation.getMemberId(),
                memberName,
                memberPhone,
                memberEmail,
                memberVip
        );

        this.id = reservation.getId();
        this.customer = customerResponse;
        this.date = reservation.getAvailableDate().getDate();
        this.time = reservation.getAvailableDate().getTime();
        this.party = reservation.getPartySize();
        this.status = reservation.getStatus();
        this.note = reservation.getSpecialRequest();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
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
