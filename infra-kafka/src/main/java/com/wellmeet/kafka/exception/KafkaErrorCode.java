package com.wellmeet.kafka.exception;

import lombok.Getter;

@Getter
public enum KafkaErrorCode {

    KAFKA_PRODUCER_ERROR(500, "Kafka 메시지 전송 실패");

    private final int statusCode;
    private final String message;

    KafkaErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
