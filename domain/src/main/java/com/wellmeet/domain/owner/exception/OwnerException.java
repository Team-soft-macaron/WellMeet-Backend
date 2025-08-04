package com.wellmeet.domain.owner.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import lombok.Getter;

@Getter
public class OwnerException extends WellMeetDomainException {

    public OwnerException(OwnerErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
