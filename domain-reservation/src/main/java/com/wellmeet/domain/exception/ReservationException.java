package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final int statusCode;

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
