package com.wellmeet.auth;

import com.wellmeet.auth.dto.LoginRequest;
import com.wellmeet.auth.dto.LoginResponse;
import com.wellmeet.auth.dto.MessageResponse;
import com.wellmeet.auth.dto.RefreshTokenRequest;
import com.wellmeet.auth.dto.RefreshTokenResponse;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerAuthService {

    private final OwnerDomainService ownerDomainService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // TODO: 실제 인증 로직 구현 필요
        // 1. 이메일로 Owner 찾기
        // 2. 비밀번호 검증
        // 3. JWT 토큰 생성

        // 임시 구현
        Owner owner = ownerDomainService.getById(1L); // 임시로 ID 1의 Owner 반환

        String token = "jwt_access_token_" + System.currentTimeMillis();
        String refreshToken = "jwt_refresh_token_" + System.currentTimeMillis();

        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.getRestaurant() != null ? Long.parseLong(owner.getRestaurant().getId()) : null,
                "owner"
        );

        return new LoginResponse(token, refreshToken, userInfo);
    }

    @Transactional
    public MessageResponse logout(Long ownerId) {
        // TODO: JWT 토큰 무효화 처리
        // 1. 토큰 블랙리스트에 추가
        // 2. Redis에서 리프레시 토큰 삭제 등

        return new MessageResponse("로그아웃되었습니다.");
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        // TODO: 리프레시 토큰 검증 및 새 토큰 발급
        // 1. 리프레시 토큰 유효성 검증
        // 2. 새로운 액세스 토큰 발급
        // 3. 필요시 리프레시 토큰도 재발급

        String newAccessToken = "new_jwt_access_token_" + System.currentTimeMillis();
        String newRefreshToken = "new_jwt_refresh_token_" + System.currentTimeMillis();

        return new RefreshTokenResponse(newAccessToken, newRefreshToken);
    }
}
