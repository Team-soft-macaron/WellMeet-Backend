package com.wellmeet.notification;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.notification.dto.NotificationListResponse;
import com.wellmeet.notification.dto.NotificationPreferencesResponse;
import com.wellmeet.notification.dto.NotificationSettingsResponse;
import com.wellmeet.notification.dto.SendNotificationRequest;
import com.wellmeet.notification.dto.UpdateNotificationPreferencesRequest;
import com.wellmeet.notification.dto.UpdateNotificationSettingsRequest;
import com.wellmeet.notification.dto.ReadNotificationResponse;
import com.wellmeet.notification.dto.ReadAllNotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final OwnerDomainService ownerDomainService;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long ownerId, int page, int limit, String type, boolean unreadOnly) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 조회 로직 구현
        // 현재는 임시 데이터 반환
        List<NotificationListResponse.Notification> notifications = new ArrayList<>();
        
        Map<String, Object> additionalData = new HashMap<>();
        additionalData.put("source", "system");
        
        NotificationListResponse.NotificationData data1 = new NotificationListResponse.NotificationData(
                123L, null, "김철수", owner.getRestaurant().getName(), additionalData);
        
        NotificationListResponse.Notification notification1 = new NotificationListResponse.Notification(
                1L,
                "RESERVATION",
                "새로운 예약 요청",
                "김철수님이 4명 예약을 요청했습니다.",
                "HIGH",
                false,
                LocalDateTime.now().minusHours(1),
                null,
                data1,
                "/reservations/123"
        );

        NotificationListResponse.NotificationData data2 = new NotificationListResponse.NotificationData(
                null, 456L, "박영희", owner.getRestaurant().getName(), additionalData);
        
        NotificationListResponse.Notification notification2 = new NotificationListResponse.Notification(
                2L,
                "REVIEW",
                "새로운 리뷰",
                "박영희님이 5점 리뷰를 남겼습니다.",
                "MEDIUM",
                true,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(12),
                data2,
                "/reviews/456"
        );

        notifications.add(notification1);
        notifications.add(notification2);

        // 타입 필터링
        if (type != null) {
            notifications = notifications.stream()
                    .filter(n -> n.getType().equals(type))
                    .toList();
        }

        // 읽지 않은 알림만 필터링
        if (unreadOnly) {
            notifications = notifications.stream()
                    .filter(n -> !n.isRead())
                    .toList();
        }

        int unreadCount = (int) notifications.stream().filter(n -> !n.isRead()).count();

        return new NotificationListResponse(notifications, unreadCount, page, 1, notifications.size());
    }

    @Transactional
    public ReadNotificationResponse markAsRead(Long ownerId, Long notificationId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 알림 읽음 처리 로직 구현
        // 1. 알림 조회
        // 2. 소유권 확인
        // 3. 읽음 상태 업데이트
        
        ReadNotificationResponse.NotificationData data = 
                new ReadNotificationResponse.NotificationData(123L, null, 456L);
        
        ReadNotificationResponse.NotificationInfo notification = 
                new ReadNotificationResponse.NotificationInfo(
                        notificationId,
                        "booking",
                        "새로운 예약",
                        "김철수님이 예약을 요청했습니다.",
                        data,
                        true,
                        LocalDateTime.now().toString()
                );
        
        return new ReadNotificationResponse(notification);
    }

    @Transactional
    public ReadAllNotificationsResponse markAllAsRead(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 모든 알림 읽음 처리 로직 구현
        // 1. 해당 사용자의 모든 읽지 않은 알림 조회
        // 2. 일괄 읽음 상태 업데이트
        
        return new ReadAllNotificationsResponse("모든 알림을 읽음 처리했습니다.", 5);
    }

    @Transactional
    public MessageResponse deleteNotification(Long ownerId, Long notificationId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 알림 삭제 로직 구현
        // 1. 알림 조회
        // 2. 소유권 확인
        // 3. 알림 삭제
        
        return new MessageResponse("알림이 삭제되었습니다.");
    }

    @Transactional
    public MessageResponse sendNotification(Long ownerId, SendNotificationRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 발송 로직 구현
        // 1. 수신자 유효성 검증
        // 2. 알림 생성
        // 3. 채널별 발송 (이메일, SMS, 푸시 등)
        // 4. 발송 결과 기록

        // 수신자 수에 따른 응답 메시지
        String message = String.format("%d명에게 알림을 발송했습니다.", request.getRecipients().size());
        
        if (!request.isSendImmediately() && request.getScheduledTime() != null) {
            message += " (예약 발송: " + request.getScheduledTime() + ")";
        }

        return new MessageResponse(message);
    }

    @Transactional(readOnly = true)
    public NotificationPreferencesResponse getNotificationPreferences(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 설정 조회 로직 구현
        // 현재는 기본값 반환
        NotificationPreferencesResponse.EmailSettings emailSettings = 
                new NotificationPreferencesResponse.EmailSettings(
                        owner.getEmail(),
                        true,
                        true,
                        "IMMEDIATE"
                );

        NotificationPreferencesResponse.SmsSettings smsSettings = 
                new NotificationPreferencesResponse.SmsSettings(
                        "010-0000-0000", // TODO: Owner 엔티티에 전화번호 필드 추가
                        false,
                        Arrays.asList("RESERVATION", "SYSTEM")
                );

        NotificationPreferencesResponse.PushSettings pushSettings = 
                new NotificationPreferencesResponse.PushSettings(
                        true,
                        true,
                        "default",
                        true
                );

        List<NotificationPreferencesResponse.NotificationType> enabledTypes = Arrays.asList(
                new NotificationPreferencesResponse.NotificationType(
                        "RESERVATION", "예약 알림", true, "HIGH", 
                        Arrays.asList("email", "sms", "push", "inapp")),
                new NotificationPreferencesResponse.NotificationType(
                        "REVIEW", "리뷰 알림", true, "MEDIUM", 
                        Arrays.asList("email", "inapp")),
                new NotificationPreferencesResponse.NotificationType(
                        "SYSTEM", "시스템 알림", true, "HIGH", 
                        Arrays.asList("email", "push", "inapp"))
        );

        NotificationPreferencesResponse.NotificationPreferences preferences = 
                new NotificationPreferencesResponse.NotificationPreferences(
                        true,  // 이메일 활성화
                        true,  // SMS 활성화
                        true,  // 푸시 활성화
                        true,  // 인앱 활성화
                        emailSettings,
                        smsSettings,
                        pushSettings,
                        enabledTypes,
                        "22:00", // 방해 금지 시작
                        "08:00", // 방해 금지 종료
                        Arrays.asList("SUNDAY") // 방해 금지 요일
                );

        return new NotificationPreferencesResponse(preferences);
    }

    @Transactional
    public NotificationPreferencesResponse updateNotificationPreferences(Long ownerId, UpdateNotificationPreferencesRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 설정 업데이트 로직 구현
        // 1. 각 설정 필드 업데이트
        // 2. 알림 채널 설정 업데이트
        // 3. 방해 금지 시간 설정 업데이트
        // 4. 알림 유형별 설정 업데이트

        return getNotificationPreferences(ownerId);
    }

    @Transactional(readOnly = true)
    public NotificationSettingsResponse getNotificationSettings(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 설정 조회 로직 구현
        // 현재는 기본값 반환
        NotificationSettingsResponse.BookingSettings booking = 
                new NotificationSettingsResponse.BookingSettings(true, true, true);
        
        NotificationSettingsResponse.ReviewSettings review = 
                new NotificationSettingsResponse.ReviewSettings(true, true);
        
        NotificationSettingsResponse.SystemSettings system = 
                new NotificationSettingsResponse.SystemSettings(true, true);
        
        NotificationSettingsResponse.MarketingSettings marketing = 
                new NotificationSettingsResponse.MarketingSettings(false, true);
        
        NotificationSettingsResponse.ChannelSettings channels = 
                new NotificationSettingsResponse.ChannelSettings(true, true, true);
        
        NotificationSettingsResponse.NotificationSettings settings = 
                new NotificationSettingsResponse.NotificationSettings(
                        booking, review, system, marketing, channels);
        
        return new NotificationSettingsResponse(settings);
    }

    @Transactional
    public NotificationSettingsResponse updateNotificationSettings(Long ownerId, UpdateNotificationSettingsRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 알림 설정 업데이트 로직 구현
        // 현재는 기본값과 요청값을 조합하여 반환
        
        NotificationSettingsResponse.BookingSettings booking = 
                new NotificationSettingsResponse.BookingSettings(
                        request.getBooking() != null && request.getBooking().getNewBooking() != null ? 
                                request.getBooking().getNewBooking() : true,
                        request.getBooking() != null && request.getBooking().getCancelled() != null ? 
                                request.getBooking().getCancelled() : true,
                        request.getBooking() != null && request.getBooking().getModified() != null ? 
                                request.getBooking().getModified() : true
                );
        
        NotificationSettingsResponse.ReviewSettings review = 
                new NotificationSettingsResponse.ReviewSettings(
                        request.getReview() != null && request.getReview().getNewReview() != null ? 
                                request.getReview().getNewReview() : true,
                        request.getReview() != null && request.getReview().getLowRating() != null ? 
                                request.getReview().getLowRating() : true
                );
        
        NotificationSettingsResponse.SystemSettings system = 
                new NotificationSettingsResponse.SystemSettings(
                        request.getSystem() != null && request.getSystem().getUpdates() != null ? 
                                request.getSystem().getUpdates() : true,
                        request.getSystem() != null && request.getSystem().getMaintenance() != null ? 
                                request.getSystem().getMaintenance() : true
                );
        
        NotificationSettingsResponse.MarketingSettings marketing = 
                new NotificationSettingsResponse.MarketingSettings(
                        request.getMarketing() != null && request.getMarketing().getPromotions() != null ? 
                                request.getMarketing().getPromotions() : false,
                        request.getMarketing() != null && request.getMarketing().getNewsletter() != null ? 
                                request.getMarketing().getNewsletter() : true
                );
        
        NotificationSettingsResponse.ChannelSettings channels = 
                new NotificationSettingsResponse.ChannelSettings(
                        request.getChannels() != null && request.getChannels().getEmail() != null ? 
                                request.getChannels().getEmail() : true,
                        request.getChannels() != null && request.getChannels().getSms() != null ? 
                                request.getChannels().getSms() : true,
                        request.getChannels() != null && request.getChannels().getPush() != null ? 
                                request.getChannels().getPush() : true
                );
        
        NotificationSettingsResponse.NotificationSettings settings = 
                new NotificationSettingsResponse.NotificationSettings(
                        booking, review, system, marketing, channels);
        
        return new NotificationSettingsResponse(settings);
    }
}