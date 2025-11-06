package com.wellmeet.saga;

public enum IdempotencyStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}