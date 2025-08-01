package com.wellmeet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryResponse {

    private List<LoginRecord> loginHistory;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRecord {
        private Long id;
        private LocalDateTime loginTime;        // 로그인 시간
        private String ipAddress;               // IP 주소
        private String userAgent;               // 브라우저/디바이스 정보
        private String location;                // 접속 위치 (예: "서울, 대한민국")
        private String deviceType;              // 디바이스 유형 (예: "Desktop", "Mobile")
        private boolean isCurrentSession;       // 현재 세션 여부
        private LocalDateTime logoutTime;       // 로그아웃 시간 (null이면 활성 세션)
        private String status;                  // 상태 (예: "SUCCESS", "FAILED", "EXPIRED")
        private String failureReason;           // 실패 이유 (실패한 경우)
    }
}