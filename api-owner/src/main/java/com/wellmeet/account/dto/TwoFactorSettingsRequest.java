package com.wellmeet.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TwoFactorSettingsRequest {
    
    @NotNull
    private Boolean enabled;           // 2단계 인증 활성화 여부
    
    private String method;             // 인증 방법 ("sms" | "email" | "app") - enabled가 true일 때 필수
}