package com.wellmeet.restaurant.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantInfoResponse {

    private RestaurantInfo restaurant;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RestaurantInfo {
        private Long id;
        private String name;
        private String category;          // 음식 카테고리
        private Address address;
        private String phone;
        private String email;
        private String description;
        private List<String> images;      // 이미지 URL 배열
        private List<String> amenities;   // 편의시설
        private int capacity;             // 수용 인원
        private int tables;               // 테이블 수
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Address {
        private String street;
        private String city;
        private String state;
        private String zipCode;
    }
}