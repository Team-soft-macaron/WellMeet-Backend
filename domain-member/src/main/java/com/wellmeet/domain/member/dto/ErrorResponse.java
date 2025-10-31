package com.wellmeet.domain.member.dto;

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
