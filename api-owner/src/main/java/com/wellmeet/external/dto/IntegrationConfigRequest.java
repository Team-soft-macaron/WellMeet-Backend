package com.wellmeet.external.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class IntegrationConfigRequest {
    
    private String provider;      // 제공업체
    private String apiKey;        // API 키
    private String apiSecret;     // API 시크릿
    private String webhookUrl;    // 웹훅 URL
    private Map<String, Object> config; // 추가 설정
}