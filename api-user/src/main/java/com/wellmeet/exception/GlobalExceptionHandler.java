package com.wellmeet.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WellMeetException.class)
    public ResponseEntity<ErrorResponse> handleWellMeetException(WellMeetException exception) {
        return toResponse(exception.getStatusCode(), exception.getMessage());
    }

    @ExceptionHandler(WellMeetDomainException.class)
    public ResponseEntity<ErrorResponse> handleWellMeetDomainException(WellMeetDomainException exception) {
        return toResponse(exception.getStatusCode(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return toResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode errorCode) {
        return toResponse(errorCode.getStatusCode(), errorCode.getMessage());
    }

    private ResponseEntity<ErrorResponse> toResponse(int statusCode, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(statusCode)
                .body(errorResponse);
    }
}
