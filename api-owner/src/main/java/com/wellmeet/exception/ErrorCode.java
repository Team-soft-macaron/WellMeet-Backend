package com.wellmeet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    INVALID_LATITUDE(400, "유효하지 않은 위도입니다."),
    INVALID_LONGITUDE(400, "유효하지 않은 경도입니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),
    MENU_NOT_FOUND(404, "메뉴를 찾을 수 없습니다."),
    CUSTOMER_NOT_FOUND(404, "고객을 찾을 수 없습니다."),
    INVALID_PASSWORD(400, "현재 비밀번호가 올바르지 않습니다."),
    PASSWORD_MISMATCH(400, "새 비밀번호와 확인 비밀번호가 일치하지 않습니다."),
    SAME_PASSWORD(400, "현재 비밀번호와 새 비밀번호가 동일합니다."),
    INVALID_VERIFICATION_CODE(400, "인증 코드가 올바르지 않습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    CORS_ORIGIN_EMPTY(500, "CORS Origin 은 적어도 한 개 있어야 합니다"),
    CORS_ORIGIN_STRING_BLANK(500, "CORS Origin 에 빈 값이 들어올 수 없습니다"),
    ;

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}