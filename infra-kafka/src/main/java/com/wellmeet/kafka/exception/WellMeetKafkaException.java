package com.wellmeet.kafka.exception;

import lombok.Getter;

@Getter
public class WellMeetKafkaException extends RuntimeException {

    private final int statusCode;

    public WellMeetKafkaException(KafkaErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
