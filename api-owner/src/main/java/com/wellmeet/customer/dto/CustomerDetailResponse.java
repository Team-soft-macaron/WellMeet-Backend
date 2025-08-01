package com.wellmeet.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailResponse {

    private CustomerDetail customer;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDetail {
        private Long id;
        private String name;
        private String phone;
        private String email;
        private boolean isVip;
        private int visitCount;
        private LocalDateTime lastVisit;
        private BigDecimal totalSpent;
        private double averagePartySize;
        private String preferences;
        private String allergies;
        private LocalDate birthday;
        private String notes;
        private List<String> tags;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
