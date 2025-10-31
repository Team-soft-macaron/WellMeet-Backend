package com.wellmeet.domain.owner.exception;

import lombok.Getter;

@Getter
public class OwnerException extends RuntimeException {

    private final int statusCode;

    public OwnerException(OwnerErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
