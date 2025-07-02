package com.wellmeet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_LATITUDE(HttpStatus.BAD_REQUEST, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(HttpStatus.BAD_REQUEST, "유효하지 않은 경도입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레스토랑을 찾을 수 없습니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
