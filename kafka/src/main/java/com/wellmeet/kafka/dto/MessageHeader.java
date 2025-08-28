package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageHeader {

    private final String messageId;
    private final String version;
    private final LocalDateTime timestamp;
    private final String source;

    public MessageHeader(String version, String source) {
        this.messageId = UUID.randomUUID().toString();
        this.version = version;
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }
}
