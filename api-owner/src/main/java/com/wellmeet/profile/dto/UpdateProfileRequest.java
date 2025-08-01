package com.wellmeet.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 1, max = 50, message = "이름은 1-50자 사이여야 합니다.")
    private String name;

    @Email(message = "올바른 이메일 형식이어야 합니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다.")
    private String phone;

    @Size(max = 50, message = "직책은 50자 이하여야 합니다.")
    private String position;

    private Address address;

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