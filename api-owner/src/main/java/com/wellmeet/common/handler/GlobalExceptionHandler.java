package com.wellmeet.common.handler;

import com.wellmeet.common.dto.ErrorResponse;
import com.wellmeet.exception.WellMeetException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 전역 예외 처리기
 * 모든 예외를 일관된 형식으로 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * WellMeet 커스텀 예외 처리
     */
    @ExceptionHandler(WellMeetException.class)
    public ResponseEntity<ErrorResponse> handleWellMeetException(WellMeetException e, HttpServletRequest request) {
        log.error("WellMeetException occurred: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            e.getMessage(),
            e.getErrorCode().name(),
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.valueOf(e.getStatusCode()))
            .body(errorResponse);
    }

    /**
     * 검증 예외 처리 (@Valid)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        
        log.error("Validation error occurred: {}", e.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.FieldError.of(
                error.getField(),
                error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            "입력값 검증에 실패했습니다.",
            "VALIDATION_FAILED",
            request.getRequestURI(),
            fieldErrors
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest request) {
        log.error("Binding error occurred: {}", e.getMessage());
        
        List<ErrorResponse.FieldError> fieldErrors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> ErrorResponse.FieldError.of(
                error.getField(),
                error.getRejectedValue() != null ? error.getRejectedValue().toString() : null,
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            "요청 파라미터 바인딩에 실패했습니다.",
            "BINDING_FAILED",
            request.getRequestURI(),
            fieldErrors
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            "서버 내부 오류가 발생했습니다.",
            "INTERNAL_SERVER_ERROR",
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(errorResponse);
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException e, HttpServletRequest request) {
        
        log.error("IllegalArgumentException occurred: {}", e.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.of(
            e.getMessage(),
            "INVALID_ARGUMENT",
            request.getRequestURI()
        );
        
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(errorResponse);
    }
}