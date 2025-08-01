package com.wellmeet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecuritySettingsResponse {

    private SecuritySettings security;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SecuritySettings {
        private boolean twoFactorEnabled;           // 2단계 인증 활성화 여부
        private String twoFactorMethod;             // 2단계 인증 방법 (예: "SMS", "EMAIL", "APP")
        private boolean loginNotificationEnabled;   // 로그인 알림 활성화 여부
        private boolean suspiciousActivityAlert;    // 의심스러운 활동 알림
        private int sessionTimeoutMinutes;          // 세션 타임아웃 (분)
        private boolean requirePasswordForSensitive; // 민감한 작업 시 비밀번호 재확인 요구
        private LoginRestrictions loginRestrictions; // 로그인 제한 설정
        private LocalDateTime lastPasswordChange;    // 마지막 비밀번호 변경 시간
        private int activeSessionCount;             // 활성 세션 수
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRestrictions {
        private boolean ipWhitelistEnabled;         // IP 화이트리스트 활성화
        private java.util.List<String> allowedIps; // 허용된 IP 목록
        private boolean timeRestrictionEnabled;     // 시간 제한 활성화
        private String allowedTimeStart;            // 허용 시간 시작 (HH:mm)
        private String allowedTimeEnd;              // 허용 시간 종료 (HH:mm)
        private java.util.List<String> allowedDays; // 허용 요일 (예: ["MONDAY", "TUESDAY"])
    }
}