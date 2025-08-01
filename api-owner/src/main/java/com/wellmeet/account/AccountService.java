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
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final OwnerDomainService ownerDomainService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MessageResponse changePassword(Long ownerId, ChangePasswordRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new WellMeetException(ErrorCode.INVALID_PASSWORD);
        }

        // 새 비밀번호와 확인 비밀번호 일치 확인
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new WellMeetException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 현재 비밀번호와 새 비밀번호가 같은지 확인
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new WellMeetException(ErrorCode.SAME_PASSWORD);
        }

        // 비밀번호 변경
        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        owner.changePassword(encodedPassword);
        ownerDomainService.save(owner);

        return new MessageResponse("비밀번호가 성공적으로 변경되었습니다.");
    }

    @Transactional(readOnly = true)
    public LoginHistoryResponse getLoginHistory(Long ownerId, int page, int limit) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 로그인 기록 조회 로직 구현
        // 현재는 임시 데이터 반환
        List<LoginHistoryResponse.LoginRecord> records = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            LoginHistoryResponse.LoginRecord record = new LoginHistoryResponse.LoginRecord(
                    (long) (i + 1),
                    LocalDateTime.now().minusDays(i),
                    "192.168.1." + (100 + i),
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
                    "서울, 대한민국",
                    "Desktop",
                    i == 0, // 첫 번째 항목이 현재 세션
                    i == 0 ? null : LocalDateTime.now().minusDays(i).plusHours(2),
                    "SUCCESS",
                    null
            );
            records.add(record);
        }

        return new LoginHistoryResponse(records, page, 1, records.size());
    }

    @Transactional(readOnly = true)
    public SessionsResponse getSessions(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 세션 조회 로직 구현
        // 현재는 임시 데이터 반환
        List<SessionsResponse.SessionInfo> sessions = Arrays.asList(
            new SessionsResponse.SessionInfo(
                "session-1",
                "MacBook Pro",
                "Chrome 91.0",
                "192.168.1.100",
                "서울, 대한민국",
                LocalDateTime.now().minusMinutes(5).toString(),
                true
            ),
            new SessionsResponse.SessionInfo(
                "session-2",
                "iPhone 12",
                "Safari 14.0",
                "192.168.1.101",
                "서울, 대한민국",
                LocalDateTime.now().minusHours(2).toString(),
                false
            )
        );
        
        return new SessionsResponse(sessions);
    }

    @Transactional
    public MessageResponse terminateSession(Long ownerId, String sessionId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 실제 세션 종료 로직 구현
        // JWT의 경우 해당 토큰 무효화
        
        return new MessageResponse("세션이 종료되었습니다.");
    }

    @Transactional
    public TerminateSessionsResponse terminateOtherSessions(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        
        // TODO: 현재 세션을 제외한 다른 세션들 종료
        // JWT의 경우 현재 토큰을 제외한 나머지 토큰들 무효화
        
        return new TerminateSessionsResponse("다른 모든 세션이 종료되었습니다.", 2);
    }

    @Transactional(readOnly = true)
    public SecuritySettingsResponse getSecuritySettings(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 보안 설정 조회 로직 구현
        // 현재는 기본값 반환
        SecuritySettingsResponse.LoginRestrictions restrictions = 
                new SecuritySettingsResponse.LoginRestrictions(
                        false,
                        new ArrayList<>(),
                        false,
                        "09:00",
                        "18:00",
                        Arrays.asList("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY")
                );

        SecuritySettingsResponse.SecuritySettings settings = 
                new SecuritySettingsResponse.SecuritySettings(
                        false,        // 2단계 인증 비활성화
                        null,         // 2단계 인증 방법 없음
                        true,         // 로그인 알림 활성화
                        true,         // 의심스러운 활동 알림 활성화
                        120,          // 세션 타임아웃 2시간
                        true,         // 민감한 작업 시 비밀번호 재확인 요구
                        restrictions,
                        owner.getUpdatedAt(), // 마지막 업데이트 시간으로 대체
                        1             // 활성 세션 수
                );

        return new SecuritySettingsResponse(settings);
    }

    @Transactional
    public SecuritySettingsResponse updateSecuritySettings(Long ownerId, UpdateSecuritySettingsRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 보안 설정 업데이트 로직 구현
        // 현재는 업데이트 후 현재 설정 반환
        
        return getSecuritySettings(ownerId);
    }

    @Transactional
    public MessageResponse enableTwoFactorAuth(Long ownerId, TwoFactorAuthRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new WellMeetException(ErrorCode.INVALID_PASSWORD);
        }

        // TODO: 실제 2단계 인증 활성화 로직 구현
        // 1. 인증 방법에 따라 설정 (SMS, EMAIL, APP)
        // 2. 인증 코드 검증
        // 3. 설정 저장

        if (request.getVerificationCode() == null || !request.getVerificationCode().equals("123456")) {
            throw new WellMeetException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        return new MessageResponse("2단계 인증이 성공적으로 활성화되었습니다.");
    }

    @Transactional
    public void disableTwoFactorAuth(Long ownerId, TwoFactorAuthRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(request.getCurrentPassword(), owner.getPassword())) {
            throw new WellMeetException(ErrorCode.INVALID_PASSWORD);
        }

        // TODO: 실제 2단계 인증 비활성화 로직 구현
        // 1. 인증 코드 검증
        // 2. 2단계 인증 설정 제거

        if (request.getVerificationCode() == null || !request.getVerificationCode().equals("123456")) {
            throw new WellMeetException(ErrorCode.INVALID_VERIFICATION_CODE);
        }
    }

    @Transactional
    public TwoFactorSettingsResponse updateTwoFactorAuth(Long ownerId, TwoFactorSettingsRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // TODO: 실제 2단계 인증 설정 업데이트 로직 구현
        // 1. enabled가 true면 인증 활성화, false면 비활성화
        // 2. method에 따라 적절한 인증 방법 설정
        // 3. TOTP 앱 사용 시 QR 코드 및 시크릿 키 생성

        TwoFactorSettingsResponse.TwoFactorSettings settings;
        
        if (request.getEnabled()) {
            String method = request.getMethod() != null ? request.getMethod() : "app";
            String qrCode = method.equals("app") ? "https://example.com/qr-code" : null;
            String secret = method.equals("app") ? "JBSWY3DPEHPK3PXP" : null;
            
            settings = new TwoFactorSettingsResponse.TwoFactorSettings(
                true, method, qrCode, secret);
        } else {
            settings = new TwoFactorSettingsResponse.TwoFactorSettings(
                false, null, null, null);
        }
        
        return new TwoFactorSettingsResponse(settings);
    }

    @Transactional
    public MessageResponse deleteAccount(Long ownerId, DeleteAccountRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), owner.getPassword())) {
            throw new WellMeetException(ErrorCode.INVALID_PASSWORD);
        }

        // TODO: 실제 계정 삭제 로직 구현
        // 1. 모든 관련 데이터 백업 또는 익명화
        // 2. 예약, 리뷰 등 관련 데이터 처리
        // 3. 계정 비활성화 또는 삭제
        // 4. 탈퇴 사유 및 피드백 저장

        return new MessageResponse("계정이 성공적으로 삭제되었습니다.");
    }
}