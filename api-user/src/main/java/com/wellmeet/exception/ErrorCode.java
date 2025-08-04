package com.wellmeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    CORS_ORIGIN_EMPTY(500, "CORS Origin 은 적어도 한 개 있어야 합니다"),
    CORS_ORIGIN_STRING_BLANK(500, "CORS Origin 에 빈 값이 들어올 수 없습니다"),
    FIELD_ERROR(400, "입력이 잘못되었습니다."),
    URL_PARAMETER_ERROR(400, "입력이 잘못되었습니다."),
    METHOD_ARGUMENT_TYPE_MISMATCH(400, "입력한 값의 타입이 잘못되었습니다."),
    ;

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
