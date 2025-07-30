package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public enum DomainErrorCode {

    INVALID_LATITUDE(400, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(400, "유효하지 않은 경도입니다."),
    INVALID_RATING(400, "유효하지 않은 평점입니다."),
    ;

    private final int statusCode;
    private final String message;

    DomainErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
