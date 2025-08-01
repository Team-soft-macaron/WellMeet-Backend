package com.wellmeet.exception;

import lombok.Getter;

@Getter
public class WellMeetException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int statusCode;

    public WellMeetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.statusCode = errorCode.getStatusCode();
    }
}