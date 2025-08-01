package com.wellmeet.customer.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerReviewHistoryResponse {

    private List<ReviewHistory> reviews;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewHistory {
        private Long id;
        private double rating;        // 1-5
        private String comment;
        private String reply;         // 사장님 답글
        private LocalDateTime createdAt;
        private Long bookingId;       // 관련 예약 ID
    }
}