package com.wellmeet.reservation.dto;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookingDetailResponse {
    
    private BookingDetail booking;
    
    @Getter
    @NoArgsConstructor
    public static class BookingDetail {
        private Long id;
        private CustomerDetail customer;
        private LocalDate date;
        private LocalTime time;
        private int party;
        private ReservationStatus status;
        private Integer tableNumber;
        private String specialRequests;
        private List<OrderItem> orderHistory;
        private BigDecimal totalAmount;
        private BigDecimal deposit;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private String createdBy;
    }
    
    @Getter
    @NoArgsConstructor
    public static class CustomerDetail {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private boolean isVip;
        private int visitCount;
        private LocalDate lastVisit;
        private String preferences;
        private String allergies;
        
        public CustomerDetail(Long id, String name, String phone, String email, boolean isVip,
                             int visitCount, LocalDate lastVisit, String preferences, String allergies) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.isVip = isVip;
            this.visitCount = visitCount;
            this.lastVisit = lastVisit;
            this.preferences = preferences;
            this.allergies = allergies;
        }
    }
    
    @Getter
    @NoArgsConstructor
    public static class OrderItem {
        private String menuItem;
        private int quantity;
        private BigDecimal price;
        
        public OrderItem(String menuItem, int quantity, BigDecimal price) {
            this.menuItem = menuItem;
            this.quantity = quantity;
            this.price = price;
        }
    }
    
    public BookingDetailResponse(Reservation reservation, CustomerDetail customer, 
                                List<OrderItem> orderHistory) {
        this.booking = new BookingDetail();
        this.booking.id = reservation.getId();
        this.booking.customer = customer;
        this.booking.date = reservation.getReservationDateTime().toLocalDate();
        this.booking.time = reservation.getReservationDateTime().toLocalTime();
        this.booking.party = reservation.getPartySize();
        this.booking.status = reservation.getStatus();
        this.booking.tableNumber = null;
        this.booking.specialRequests = reservation.getSpecialRequest();
        this.booking.orderHistory = orderHistory;
        this.booking.totalAmount = orderHistory.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.booking.deposit = BigDecimal.ZERO;
        this.booking.createdAt = reservation.getCreatedAt();
        this.booking.updatedAt = reservation.getUpdatedAt();
        this.booking.createdBy = "SYSTEM";
    }
}