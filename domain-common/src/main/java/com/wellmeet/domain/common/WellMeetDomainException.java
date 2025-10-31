package com.wellmeet.domain.common;

import lombok.Getter;

@Getter
public class WellMeetDomainException extends RuntimeException {

    private final int statusCode;

    public WellMeetDomainException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}