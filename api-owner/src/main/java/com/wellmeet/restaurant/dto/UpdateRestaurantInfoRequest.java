package com.wellmeet.restaurant.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRestaurantInfoRequest {

    @Size(min = 1, max = 100, message = "매장명은 1-100자 사이여야 합니다.")
    private String name;

    @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
    private String category;

    private Address address;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phone;

    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Size(max = 1000, message = "설명은 1000자 이하여야 합니다.")
    private String description;

    private List<String> amenities;

    @Min(value = 1, message = "수용 인원은 1명 이상이어야 합니다.")
    private Integer capacity;

    @Min(value = 1, message = "테이블 수는 1개 이상이어야 합니다.")
    private Integer tables;

    @Getter
    @NoArgsConstructor
    public static class Address {
        @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
        private String street;
        
        @Size(max = 50, message = "시/도는 50자 이하여야 합니다.")
        private String city;
        
        @Size(max = 50, message = "시/군/구는 50자 이하여야 합니다.")
        private String state;
        
        @Size(max = 10, message = "우편번호는 10자 이하여야 합니다.")
        private String zipCode;
    }
}