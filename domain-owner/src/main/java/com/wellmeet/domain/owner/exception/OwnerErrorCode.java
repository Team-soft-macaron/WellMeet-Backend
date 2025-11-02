package com.wellmeet.domain.owner.exception;

import lombok.Getter;

@Getter
public enum OwnerErrorCode {

    OWNER_NOT_FOUND(404, "존재하지 않는 사업자입니다."),
    OWNER_NAME_INVALID(400, "유효하지 않은 이름입니다."),
    ;

    private final int statusCode;
    private final String message;

    OwnerErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
