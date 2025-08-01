package com.wellmeet.account.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateSecuritySettingsRequest {

    private Boolean loginNotificationEnabled;    // 로그인 알림 활성화 여부
    private Boolean suspiciousActivityAlert;     // 의심스러운 활동 알림
    
    @Min(value = 15, message = "세션 타임아웃은 최소 15분이어야 합니다.")
    @Max(value = 480, message = "세션 타임아웃은 최대 480분(8시간)이어야 합니다.")
    private Integer sessionTimeoutMinutes;       // 세션 타임아웃 (분)
    
    private Boolean requirePasswordForSensitive; // 민감한 작업 시 비밀번호 재확인 요구
    private LoginRestrictionsUpdate loginRestrictions; // 로그인 제한 설정

    @Getter
    @NoArgsConstructor
    public static class LoginRestrictionsUpdate {
        private Boolean ipWhitelistEnabled;         // IP 화이트리스트 활성화
        private List<String> allowedIps;           // 허용된 IP 목록
        private Boolean timeRestrictionEnabled;     // 시간 제한 활성화
        private String allowedTimeStart;            // 허용 시간 시작 (HH:mm)
        private String allowedTimeEnd;              // 허용 시간 종료 (HH:mm)
        private List<String> allowedDays;          // 허용 요일
    }
}