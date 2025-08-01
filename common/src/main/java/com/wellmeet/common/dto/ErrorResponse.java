package com.wellmeet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 표준 에러 응답 클래스
 * 모든 에러 응답을 일관된 형식으로 제공합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private boolean success = false;        // 항상 false
    private String message;                 // 에러 메시지
    private String errorCode;               // 에러 코드
    private LocalDateTime timestamp;        // 에러 발생 시간
    private String path;                    // 요청 경로
    private List<FieldError> fieldErrors;   // 필드 검증 에러 목록
    
    /**
     * 기본 에러 응답 생성
     */
    public static ErrorResponse of(String message, String errorCode, String path) {
        return new ErrorResponse(
            false,
            message,
            errorCode,
            LocalDateTime.now(),
            path,
            null
        );
    }
    
    /**
     * 필드 검증 에러를 포함한 응답 생성
     */
    public static ErrorResponse of(String message, String errorCode, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(
            false,
            message,
            errorCode,
            LocalDateTime.now(),
            path,
            fieldErrors
        );
    }
    
    /**
     * 필드 검증 에러 클래스
     */
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;         // 필드 이름
        private String rejectedValue; // 거부된 값
        private String message;       // 에러 메시지
        
        public static FieldError of(String field, String rejectedValue, String message) {
            return new FieldError(field, rejectedValue, message);
        }
    }
}