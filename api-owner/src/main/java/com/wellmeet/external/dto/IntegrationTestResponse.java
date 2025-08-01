package com.wellmeet.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationTestResponse {
    
    private boolean success;
    private String message;
    private TestDetails details;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestDetails {
        private Long latency;      // 응답 시간 (ms)
        private String version;    // API 버전
        private List<String> features; // 지원 기능
    }
}