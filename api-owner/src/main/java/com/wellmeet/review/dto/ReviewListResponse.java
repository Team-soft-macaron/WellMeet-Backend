package com.wellmeet.review.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResponse {

    private Long id;
    private CustomerInfo customer;
    private BookingInfo booking;
    private double rating;
    private String comment;
    private ReplyInfo reply;
    private List<String> images;
    private LocalDateTime createdAt;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private Long id;
        private String name;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingInfo {
        private Long id;
        private String date;
        private int party;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyInfo {
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
