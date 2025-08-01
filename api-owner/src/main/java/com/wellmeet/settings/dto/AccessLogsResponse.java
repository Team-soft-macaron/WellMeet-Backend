package com.wellmeet.settings.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogsResponse {
    
    private List<AccessLog> logs;
    private long total;
    private int page;
    private int totalPages;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccessLog {
        private Long id;
        private Long userId;
        private String userName;
        private String action;          // 수행한 작업
        private String resource;        // 대상 리소스
        private Object details;         // 상세 정보
        private String ipAddress;
        private String timestamp;
    }
}