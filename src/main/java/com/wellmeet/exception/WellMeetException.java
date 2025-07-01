package com.wellmeet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class WellMeetException extends RuntimeException {

    private final HttpStatus status;

    public WellMeetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
    }
}
