package com.wellmeet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 표준 API 응답 래퍼 클래스
 * 모든 API 응답을 일관된 형식으로 래핑합니다.
 *
 * @param <T> 실제 데이터 타입
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;              // 성공 여부
    private String message;               // 응답 메시지
    private T data;                       // 실제 데이터
    private LocalDateTime timestamp;      // 응답 시간
    private String requestPath;           // 요청 경로 (선택적)
    
    /**
     * 성공 응답 생성
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
            true, 
            "성공", 
            data, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * 성공 응답 생성 (메시지 포함)
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
            true, 
            message, 
            data, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * 실패 응답 생성
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(
            false, 
            message, 
            null, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * 실패 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> failure(String message, T data) {
        return new ApiResponse<>(
            false, 
            message, 
            data, 
            LocalDateTime.now(), 
            null
        );
    }
    
    /**
     * 요청 경로를 포함한 응답 생성
     */
    public static <T> ApiResponse<T> success(T data, String message, String requestPath) {
        return new ApiResponse<>(
            true, 
            message, 
            data, 
            LocalDateTime.now(), 
            requestPath
        );
    }
    
    /**
     * 메시지만 포함한 성공 응답
     */
    public static ApiResponse<Void> successMessage(String message) {
        return new ApiResponse<>(
            true, 
            message, 
            null, 
            LocalDateTime.now(), 
            null
        );
    }
}