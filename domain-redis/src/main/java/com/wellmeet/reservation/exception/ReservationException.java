package com.wellmeet.reservation.exception;

import com.wellmeet.config.WellMeetRedisException;
import lombok.Getter;

@Getter
public class ReservationException extends WellMeetRedisException {

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
