package com.wellmeet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WellMeetException.class)
    public ResponseEntity<ErrorResponse> handleOAuthClientException(WellMeetException exception) {
        return toResponse(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return toResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> toResponse(ErrorCode errorCode) {
        return toResponse(errorCode.getStatus(), errorCode.getMessage());
    }

    private ResponseEntity<ErrorResponse> toResponse(HttpStatus httpStatus, String message) {
        ErrorResponse errorResponse = new ErrorResponse(message);
        return ResponseEntity.status(httpStatus)
                .body(errorResponse);
    }
}
