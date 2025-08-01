package com.wellmeet.notification.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateNotificationSettingsRequest {
    
    private BookingSettings booking;
    private ReviewSettings review;
    private SystemSettings system;
    private MarketingSettings marketing;
    private ChannelSettings channels;
    
    @Getter
    @NoArgsConstructor
    public static class BookingSettings {
        private Boolean newBooking;        // 새 예약 알림
        private Boolean cancelled;         // 예약 취소 알림
        private Boolean modified;          // 예약 변경 알림
    }
    
    @Getter
    @NoArgsConstructor
    public static class ReviewSettings {
        private Boolean newReview;         // 새 리뷰 알림
        private Boolean lowRating;         // 낮은 평점 알림 (3점 이하)
    }
    
    @Getter
    @NoArgsConstructor
    public static class SystemSettings {
        private Boolean updates;           // 시스템 업데이트 알림
        private Boolean maintenance;       // 점검 알림
    }
    
    @Getter
    @NoArgsConstructor
    public static class MarketingSettings {
        private Boolean promotions;        // 프로모션 알림
        private Boolean newsletter;        // 뉴스레터
    }
    
    @Getter
    @NoArgsConstructor
    public static class ChannelSettings {
        private Boolean email;             // 이메일 알림
        private Boolean sms;               // SMS 알림
        private Boolean push;              // 푸시 알림
    }
}