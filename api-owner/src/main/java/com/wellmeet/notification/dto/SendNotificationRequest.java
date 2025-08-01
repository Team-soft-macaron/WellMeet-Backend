package com.wellmeet.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class SendNotificationRequest {

    @NotEmpty(message = "수신자 목록은 필수입니다.")
    private List<String> recipients;           // 수신자 목록 (고객 ID 또는 이메일)
    
    private String recipientType;              // 수신자 유형 (예: "ALL", "VIP", "RECENT", "CUSTOM")

    @NotBlank(message = "알림 제목은 필수입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    private String title;                      // 알림 제목

    @NotBlank(message = "알림 메시지는 필수입니다.")
    @Size(max = 1000, message = "메시지는 1000자 이하여야 합니다.")
    private String message;                    // 알림 메시지

    private String type;                       // 알림 유형 (예: "PROMOTION", "ANNOUNCEMENT", "EVENT")
    private String priority;                   // 우선순위 (예: "HIGH", "MEDIUM", "LOW")
    private String actionUrl;                  // 액션 URL
    private Map<String, Object> additionalData; // 추가 데이터
    private boolean sendImmediately;           // 즉시 발송 여부
    private String scheduledTime;              // 예약 발송 시간 (ISO 8601 형식)
}