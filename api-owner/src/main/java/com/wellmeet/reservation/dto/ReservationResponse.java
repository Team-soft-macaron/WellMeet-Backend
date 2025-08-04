package com.wellmeet.reservation.dto;

import com.wellmeet.domain.member.entity.Member;
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
    private Integer tableNumber;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ReservationResponse(Reservation reservation) {
        Member member = reservation.getMember();
        CustomerSummaryResponse customerResponse = new CustomerSummaryResponse(member);

        this.id = reservation.getId();
        this.customer = customerResponse;
        this.date = reservation.getAvailableDate().getDate();
        this.time = reservation.getAvailableDate().getTime();
        this.party = reservation.getPartySize();
        this.status = reservation.getStatus();
        this.tableNumber = null;
        this.note = reservation.getSpecialRequest();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
    }

    @Getter
    @NoArgsConstructor
    public static class CustomerSummaryResponse {

        private Long id;
        private String name;
        private String phone;
        private String email;
        private boolean vip;

        public CustomerSummaryResponse(Member member) {
            this.id = member.getId();
            this.name = member.getName();
            this.phone = member.getPhone();
            this.email = member.getEmail();
            this.vip = member.isVip();
        }
    }
}
