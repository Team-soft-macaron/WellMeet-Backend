package com.wellmeet.settings;

import com.wellmeet.settings.dto.BusinessSettingsResponse;
import com.wellmeet.settings.dto.SystemSettingsResponse;
import com.wellmeet.settings.dto.UpdateBusinessSettingsRequest;
import com.wellmeet.settings.dto.UpdateSystemSettingsRequest;
import com.wellmeet.settings.dto.BookingPolicyResponse;
import com.wellmeet.settings.dto.UpdateBookingPolicyRequest;
import com.wellmeet.settings.dto.AccessLogsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping("/business")
    public BusinessSettingsResponse getBusinessSettings(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return settingsService.getBusinessSettings(ownerId);
    }

    @PatchMapping("/business")
    public BusinessSettingsResponse updateBusinessSettings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateBusinessSettingsRequest request
    ) {
        return settingsService.updateBusinessSettings(ownerId, request);
    }

    @GetMapping("/system")
    public SystemSettingsResponse getSystemSettings(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return settingsService.getSystemSettings(ownerId);
    }

    @PatchMapping("/system")
    public SystemSettingsResponse updateSystemSettings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateSystemSettingsRequest request
    ) {
        return settingsService.updateSystemSettings(ownerId, request);
    }

    @GetMapping("/booking-policy")
    public BookingPolicyResponse getBookingPolicy(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return settingsService.getBookingPolicy(ownerId);
    }

    @PatchMapping("/booking-policy")
    public BookingPolicyResponse updateBookingPolicy(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateBookingPolicyRequest request
    ) {
        return settingsService.updateBookingPolicy(ownerId, request);
    }

    @GetMapping("/access-logs")
    public AccessLogsResponse getAccessLogs(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action
    ) {
        return settingsService.getAccessLogs(ownerId, page, limit, userId, action);
    }
}