package com.wellmeet.reservation.exception;

import lombok.Getter;

@Getter
public enum ReservationErrorCode {

    ALREADY_RESERVING(400, "예약 중 입니다."),
    ;

    private final int statusCode;
    private final String message;

    ReservationErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
