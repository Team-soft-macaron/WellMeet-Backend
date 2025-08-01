package com.wellmeet.auth;

import com.wellmeet.auth.dto.LoginRequest;
import com.wellmeet.auth.dto.LoginResponse;
import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.auth.dto.RefreshTokenRequest;
import com.wellmeet.auth.dto.RefreshTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/auth")
@RequiredArgsConstructor
public class OwnerAuthController {

    private final OwnerAuthService ownerAuthService;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return ownerAuthService.login(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public MessageResponse logout(@RequestParam Long ownerId) { // TODO: JWT에서 추출하도록 변경
        return ownerAuthService.logout(ownerId);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    public RefreshTokenResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ownerAuthService.refresh(request);
    }
}
