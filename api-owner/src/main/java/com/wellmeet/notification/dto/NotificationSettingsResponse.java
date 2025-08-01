package com.wellmeet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSettingsResponse {
    
    private NotificationSettings settings;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationSettings {
        private BookingSettings booking;
        private ReviewSettings review;
        private SystemSettings system;
        private MarketingSettings marketing;
        private ChannelSettings channels;
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSettings {
        private boolean newBooking;        // 새 예약 알림
        private boolean cancelled;         // 예약 취소 알림
        private boolean modified;          // 예약 변경 알림
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSettings {
        private boolean newReview;         // 새 리뷰 알림
        private boolean lowRating;         // 낮은 평점 알림 (3점 이하)
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SystemSettings {
        private boolean updates;           // 시스템 업데이트 알림
        private boolean maintenance;       // 점검 알림
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarketingSettings {
        private boolean promotions;        // 프로모션 알림
        private boolean newsletter;        // 뉴스레터
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChannelSettings {
        private boolean email;             // 이메일 알림
        private boolean sms;               // SMS 알림
        private boolean push;              // 푸시 알림
    }
}