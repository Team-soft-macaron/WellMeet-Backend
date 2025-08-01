package com.wellmeet.notification;

import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.notification.dto.NotificationListResponse;
import com.wellmeet.notification.dto.NotificationPreferencesResponse;
import com.wellmeet.notification.dto.NotificationSettingsResponse;
import com.wellmeet.notification.dto.SendNotificationRequest;
import com.wellmeet.notification.dto.UpdateNotificationPreferencesRequest;
import com.wellmeet.notification.dto.UpdateNotificationSettingsRequest;
import com.wellmeet.notification.dto.ReadNotificationResponse;
import com.wellmeet.notification.dto.ReadAllNotificationsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public NotificationListResponse getNotifications(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "false") boolean unreadOnly
    ) {
        return notificationService.getNotifications(ownerId, page, limit, type, unreadOnly);
    }

    @PatchMapping("/{notificationId}/read")
    public ReadNotificationResponse markAsRead(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long notificationId
    ) {
        return notificationService.markAsRead(ownerId, notificationId);
    }

    @PatchMapping("/read-all")
    public ReadAllNotificationsResponse markAllAsRead(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return notificationService.markAllAsRead(ownerId);
    }

    @DeleteMapping("/{notificationId}")
    public MessageResponse deleteNotification(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable Long notificationId
    ) {
        return notificationService.deleteNotification(ownerId, notificationId);
    }

    @PostMapping("/send")
    public MessageResponse sendNotification(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody SendNotificationRequest request
    ) {
        return notificationService.sendNotification(ownerId, request);
    }

    @GetMapping("/settings")
    public NotificationSettingsResponse getNotificationSettings(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return notificationService.getNotificationSettings(ownerId);
    }

    @PatchMapping("/settings")
    public NotificationSettingsResponse updateNotificationSettings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateNotificationSettingsRequest request
    ) {
        return notificationService.updateNotificationSettings(ownerId, request);
    }

    @GetMapping("/preferences")
    public NotificationPreferencesResponse getNotificationPreferences(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return notificationService.getNotificationPreferences(ownerId);
    }

    @PatchMapping("/preferences")
    public NotificationPreferencesResponse updateNotificationPreferences(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateNotificationPreferencesRequest request
    ) {
        return notificationService.updateNotificationPreferences(ownerId, request);
    }
}