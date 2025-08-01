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
public class BookingListResponse {

    private Long id;
    private CustomerSummary customer;
    private LocalDate date;
    private LocalTime time;
    private int party;
    private ReservationStatus status;
    private Integer tableNumber;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @NoArgsConstructor
    public static class CustomerSummary {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private boolean isVip;

        public CustomerSummary(Long id, String name, String phone, String email, boolean isVip) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.isVip = isVip;
        }
    }

    public BookingListResponse(Reservation reservation, CustomerSummary customer) {
        this.id = reservation.getId();
        this.customer = customer;
        this.date = reservation.getReservationDateTime().toLocalDate();
        this.time = reservation.getReservationDateTime().toLocalTime();
        this.party = reservation.getPartySize();
        this.status = reservation.getStatus();
        this.tableNumber = null;
        this.note = reservation.getSpecialRequest();
        this.createdAt = reservation.getCreatedAt();
        this.updatedAt = reservation.getUpdatedAt();
    }
}
