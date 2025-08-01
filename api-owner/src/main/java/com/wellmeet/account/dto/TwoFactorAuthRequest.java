package com.wellmeet.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TwoFactorAuthRequest {

    @NotBlank(message = "현재 비밀번호는 필수입니다.")
    private String currentPassword;

    private String method;              // 2단계 인증 방법 (SMS, EMAIL, APP)
    private String phoneNumber;         // SMS 인증 시 전화번호
    private String verificationCode;    // 인증 코드
}