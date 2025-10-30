package com.wellmeet.domain.reservation.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import lombok.Getter;

@Getter
public class ReservationException extends WellMeetDomainException {

    public ReservationException(ReservationErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
