package com.wellmeet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_LATITUDE(HttpStatus.BAD_REQUEST, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(HttpStatus.BAD_REQUEST, "유효하지 않은 경도입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레스토랑을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다."),
    INVALID_RATING(HttpStatus.BAD_REQUEST, "유효하지 않은 평점입니다."),
    CORS_ORIGIN_EMPTY(HttpStatus.INTERNAL_SERVER_ERROR, "CORS Origin 은 적어도 한 개 있어야 합니다"),
    CORS_ORIGIN_STRING_BLANK(HttpStatus.INTERNAL_SERVER_ERROR, "CORS Origin 에 빈 값이 들어올 수 없습니다"),
    MEMBER_RESTAURANT_NOT_FOUND(HttpStatus.NOT_FOUND, "즐겨찾기 하지 않은 레스토랑입니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
