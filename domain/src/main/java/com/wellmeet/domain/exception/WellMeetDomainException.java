package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public class WellMeetDomainException extends RuntimeException {

    private final int statusCode;

    public WellMeetDomainException(DomainErrorCode domainErrorCode) {
        super(domainErrorCode.getMessage());
        this.statusCode = domainErrorCode.getStatusCode();
    }
}
