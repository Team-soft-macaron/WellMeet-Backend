package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageHeader {

    private final String messageId;
    private final LocalDateTime timestamp;
    private final String source;

    public MessageHeader(String source) {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
}
