package com.wellmeet.exception;

import lombok.Getter;

@Getter
public class WellMeetException extends RuntimeException {

    private final int statusCode;

    public WellMeetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
