package com.wellmeet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationListResponse {

    private List<Notification> notifications;
    private int unreadCount;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Notification {
        private Long id;
        private String type;                    // 알림 유형 (예: "RESERVATION", "REVIEW", "SYSTEM")
        private String title;                   // 알림 제목
        private String message;                 // 알림 메시지
        private String priority;                // 우선순위 (예: "HIGH", "MEDIUM", "LOW")
        private boolean isRead;                 // 읽음 여부
        private LocalDateTime createdAt;        // 생성 시간
        private LocalDateTime readAt;           // 읽은 시간
        private NotificationData data;          // 추가 데이터
        private String actionUrl;               // 액션 URL (클릭 시 이동할 페이지)
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationData {
        private Long reservationId;             // 예약 관련 알림인 경우
        private Long reviewId;                  // 리뷰 관련 알림인 경우
        private String customerName;            // 고객 이름
        private String restaurantName;          // 매장 이름
        private java.util.Map<String, Object> additionalData; // 기타 추가 데이터
    }
}