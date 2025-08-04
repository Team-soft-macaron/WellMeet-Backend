package com.wellmeet.domain.reservation.exception;

import lombok.Getter;

@Getter
public enum ReservationErrorCode {

    UNAUTHORIZED_RESERVATION_ACCESS(400, "예약에 대한 권한이 없습니다."),
    PARTY_SIZE_INVALID(400, "유효하지 않은 파티 사이즈입니다."),
    REQUEST_INVALID(400, "유효하지 않은 요청입니다."),
    ;

    private final int statusCode;
    private final String message;

    ReservationErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
