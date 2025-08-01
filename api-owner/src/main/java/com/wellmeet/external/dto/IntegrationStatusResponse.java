package com.wellmeet.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationStatusResponse {
    
    private Integrations integrations;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Integrations {
        private ServiceStatus pos;        // POS 시스템
        private ServiceStatus payment;    // 결제 시스템
        private ServiceStatus delivery;   // 배달 서비스
        private ServiceStatus marketing;  // 마케팅 도구
        private ServiceStatus accounting; // 회계 시스템
    }
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceStatus {
        private boolean connected;
        private String provider;         // 제공업체명
        private String lastSync;         // 마지막 동기화
        private List<String> methods;    // 지원 결제 수단 (payment 전용)
        private List<String> platforms;  // 연동된 플랫폼 (delivery 전용)
    }
}