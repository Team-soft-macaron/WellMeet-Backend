package com.wellmeet.profile.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {

    private Profile profile;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Profile {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String position;          // 직책 (예: "대표", "매니저")
        private Long restaurantId;
        private String restaurantName;
        private String profileImage;      // 프로필 이미지 URL
        private Address address;
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