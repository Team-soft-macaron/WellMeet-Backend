package com.wellmeet.kafka.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MessageMetadata {
    private final String correlationId;
    private final String traceId;
    private final LocalDateTime createdAt;
    private final String environment;
}
