package com.wellmeet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SessionsResponse {
    
    private List<SessionInfo> sessions;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SessionInfo {
        private String id;                // 세션 ID
        private String device;            // 기기 정보
        private String browser;           // 브라우저
        private String ipAddress;         // IP 주소
        private String location;          // 위치 (도시, 국가)
        private String lastActive;        // 마지막 활동 시간
        private boolean isCurrent;        // 현재 세션 여부
    }
}