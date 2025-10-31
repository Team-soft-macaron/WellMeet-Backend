package com.wellmeet.domain.restaurant.dto;

import java.time.LocalDateTime;

public record ErrorResponse(
        int statusCode,
        String message,
        LocalDateTime timestamp
) {
    public static ErrorResponse of(int statusCode, String message) {
        return new ErrorResponse(statusCode, message, LocalDateTime.now());
    }
}
