package com.wellmeet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TwoFactorSettingsResponse {
    
    private TwoFactorSettings settings;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TwoFactorSettings {
        private boolean twoFactorEnabled;
        private String method;              // "sms" | "email" | "app"
        private String qrCode;              // TOTP 앱 사용 시 QR 코드 URL
        private String secret;              // TOTP 시크릿 키
    }
}