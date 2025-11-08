package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public class OwnerException extends RuntimeException {

    private final int statusCode;

    public OwnerException(OwnerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
