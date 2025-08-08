package com.wellmeet.config;

import lombok.Getter;

@Getter
public class WellMeetRedisException extends RuntimeException {

    private final int statusCode;

    public WellMeetRedisException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
