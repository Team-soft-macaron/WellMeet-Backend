package com.wellmeet.domain.owner.exception;

import lombok.Getter;

@Getter
public enum OwnerErrorCode {

    OWNER_NAME_INVALID(400, "유효하지 않은 이름입니다."),
    ;

    private final int statusCode;
    private final String message;

    OwnerErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
