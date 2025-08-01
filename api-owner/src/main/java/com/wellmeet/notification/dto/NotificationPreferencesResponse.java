package com.wellmeet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesResponse {

    private NotificationPreferences preferences;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationPreferences {
        private boolean emailEnabled;               // 이메일 알림 활성화
        private boolean smsEnabled;                 // SMS 알림 활성화
        private boolean pushEnabled;                // 푸시 알림 활성화
        private boolean inAppEnabled;               // 인앱 알림 활성화
        
        private EmailSettings emailSettings;       // 이메일 설정
        private SmsSettings smsSettings;           // SMS 설정
        private PushSettings pushSettings;         // 푸시 설정
        private List<NotificationType> enabledTypes; // 활성화된 알림 유형들
        
        private String quietHoursStart;            // 방해 금지 시작 시간 (HH:mm)
        private String quietHoursEnd;              // 방해 금지 종료 시간 (HH:mm)
        private List<String> quietDays;            // 방해 금지 요일
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailSettings {
        private String emailAddress;               // 알림 받을 이메일 주소
        private boolean htmlFormat;                // HTML 형식 사용 여부
        private boolean groupSimilar;              // 유사한 알림 그룹화
        private String frequency;                  // 발송 빈도 (예: "IMMEDIATE", "HOURLY", "DAILY")
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SmsSettings {
        private String phoneNumber;                // 알림 받을 전화번호
        private boolean internationalFormat;       // 국제 형식 사용 여부
        private List<String> urgentOnly;           // 긴급 알림만 받을 유형들
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PushSettings {
        private boolean soundEnabled;              // 알림음 활성화
        private boolean vibrationEnabled;          // 진동 활성화
        private String sound;                      // 알림음 선택
        private boolean showPreview;               // 미리보기 표시 여부
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationType {
        private String type;                       // 알림 유형 (예: "RESERVATION", "REVIEW", "SYSTEM")
        private String displayName;                // 표시 이름
        private boolean enabled;                   // 활성화 여부
        private String priority;                   // 우선순위
        private List<String> channels;             // 사용할 채널들 (email, sms, push, inapp)
    }
}