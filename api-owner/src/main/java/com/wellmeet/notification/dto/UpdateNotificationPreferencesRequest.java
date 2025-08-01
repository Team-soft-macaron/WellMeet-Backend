package com.wellmeet.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UpdateNotificationPreferencesRequest {

    private Boolean emailEnabled;               // 이메일 알림 활성화
    private Boolean smsEnabled;                 // SMS 알림 활성화
    private Boolean pushEnabled;                // 푸시 알림 활성화
    private Boolean inAppEnabled;               // 인앱 알림 활성화
    
    private EmailSettingsUpdate emailSettings; // 이메일 설정
    private SmsSettingsUpdate smsSettings;     // SMS 설정
    private PushSettingsUpdate pushSettings;   // 푸시 설정
    private List<NotificationTypeUpdate> enabledTypes; // 활성화된 알림 유형들
    
    private String quietHoursStart;            // 방해 금지 시작 시간 (HH:mm)
    private String quietHoursEnd;              // 방해 금지 종료 시간 (HH:mm)
    private List<String> quietDays;            // 방해 금지 요일

    @Getter
    @NoArgsConstructor
    public static class EmailSettingsUpdate {
        private String emailAddress;               // 알림 받을 이메일 주소
        private Boolean htmlFormat;                // HTML 형식 사용 여부
        private Boolean groupSimilar;              // 유사한 알림 그룹화
        private String frequency;                  // 발송 빈도
    }

    @Getter
    @NoArgsConstructor
    public static class SmsSettingsUpdate {
        private String phoneNumber;                // 알림 받을 전화번호
        private Boolean internationalFormat;       // 국제 형식 사용 여부
        private List<String> urgentOnly;           // 긴급 알림만 받을 유형들
    }

    @Getter
    @NoArgsConstructor
    public static class PushSettingsUpdate {
        private Boolean soundEnabled;              // 알림음 활성화
        private Boolean vibrationEnabled;          // 진동 활성화
        private String sound;                      // 알림음 선택
        private Boolean showPreview;               // 미리보기 표시 여부
    }

    @Getter
    @NoArgsConstructor
    public static class NotificationTypeUpdate {
        private String type;                       // 알림 유형
        private Boolean enabled;                   // 활성화 여부
        private String priority;                   // 우선순위
        private List<String> channels;             // 사용할 채널들
    }
}