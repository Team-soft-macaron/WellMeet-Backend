package com.wellmeet.account;

import com.wellmeet.account.dto.ChangePasswordRequest;
import com.wellmeet.account.dto.LoginHistoryResponse;
import com.wellmeet.account.dto.SecuritySettingsResponse;
import com.wellmeet.account.dto.TwoFactorAuthRequest;
import com.wellmeet.account.dto.TwoFactorSettingsRequest;
import com.wellmeet.account.dto.TwoFactorSettingsResponse;
import com.wellmeet.account.dto.UpdateSecuritySettingsRequest;
import com.wellmeet.account.dto.SessionsResponse;
import com.wellmeet.account.dto.TerminateSessionsResponse;
import com.wellmeet.account.dto.DeleteAccountRequest;
import com.wellmeet.auth.dto.MessageResponse;
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
@RequestMapping("/owner/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PatchMapping("/password")
    public MessageResponse changePassword(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        return accountService.changePassword(ownerId, request);
    }

    @GetMapping("/login-history")
    public LoginHistoryResponse getLoginHistory(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return accountService.getLoginHistory(ownerId, page, limit);
    }

    @GetMapping("/sessions")
    public SessionsResponse getSessions(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return accountService.getSessions(ownerId);
    }

    @DeleteMapping("/sessions/{sessionId}")
    public MessageResponse terminateSession(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @PathVariable String sessionId
    ) {
        return accountService.terminateSession(ownerId, sessionId);
    }

    @DeleteMapping("/sessions/others")
    public TerminateSessionsResponse terminateOtherSessions(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return accountService.terminateOtherSessions(ownerId);
    }

    @GetMapping("/security")
    public SecuritySettingsResponse getSecuritySettings(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return accountService.getSecuritySettings(ownerId);
    }

    @PatchMapping("/security")
    public SecuritySettingsResponse updateSecuritySettings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody UpdateSecuritySettingsRequest request
    ) {
        return accountService.updateSecuritySettings(ownerId, request);
    }

    @PatchMapping("/two-factor")
    public TwoFactorSettingsResponse updateTwoFactorAuth(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody TwoFactorSettingsRequest request
    ) {
        return accountService.updateTwoFactorAuth(ownerId, request);
    }

    @DeleteMapping
    public MessageResponse deleteAccount(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        return accountService.deleteAccount(ownerId, request);
    }

    // Legacy endpoints for backward compatibility
    @PostMapping("/2fa/enable")
    public MessageResponse enableTwoFactorAuth(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody TwoFactorAuthRequest request
    ) {
        return accountService.enableTwoFactorAuth(ownerId, request);
    }

    @DeleteMapping("/2fa")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disableTwoFactorAuth(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @Valid @RequestBody TwoFactorAuthRequest request
    ) {
        accountService.disableTwoFactorAuth(ownerId, request);
    }
}