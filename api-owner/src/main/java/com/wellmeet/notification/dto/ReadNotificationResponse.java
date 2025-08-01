package com.wellmeet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadNotificationResponse {
    
    private NotificationInfo notification;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationInfo {
        private Long id;
        private String type;               // 알림 유형
        private String title;              // 제목
        private String message;            // 내용
        private NotificationData data;     // 관련 데이터
        private boolean isRead;            // 읽음 여부
        private String createdAt;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationData {
        private Long bookingId;
        private Long reviewId;
        private Long customerId;
    }
}