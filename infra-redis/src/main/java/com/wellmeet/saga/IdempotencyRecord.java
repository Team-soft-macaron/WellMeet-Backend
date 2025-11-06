package com.wellmeet.saga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdempotencyRecord {

    private String sagaId;
    private IdempotencyStatus status;
    private Object response;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
