package com.wellmeet.domain.member.exception;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {

    private final int statusCode;

    public MemberException(MemberErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
