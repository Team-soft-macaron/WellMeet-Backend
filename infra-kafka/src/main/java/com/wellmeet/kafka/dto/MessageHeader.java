package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MessageHeader {

    private final String messageId;
    private final LocalDateTime timestamp;

    public MessageHeader() {
        this.messageId = UUID.randomUUID().toString();
        this.timestamp = LocalDateTime.now();
    }
}
