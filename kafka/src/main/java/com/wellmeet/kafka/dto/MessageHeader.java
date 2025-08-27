package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageHeader {
    private final String messageId;
    private final String version;
    private final LocalDateTime timestamp;
    private final String source;
}
