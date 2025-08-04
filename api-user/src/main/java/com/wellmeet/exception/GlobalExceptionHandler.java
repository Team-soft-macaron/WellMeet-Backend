package com.wellmeet.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindingException(BindException exception) {
        return toResponse(ErrorCode.FIELD_ERROR);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        return toResponse(ErrorCode.URL_PARAMETER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {
        return toResponse(ErrorCode.METHOD_ARGUMENT_TYPE_MISMATCH);
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
