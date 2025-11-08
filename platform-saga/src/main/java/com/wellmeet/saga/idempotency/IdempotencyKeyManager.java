package com.wellmeet.saga.idempotency;

import com.wellmeet.saga.IdempotencyRecord;
import com.wellmeet.saga.IdempotencyStatus;
import com.wellmeet.saga.SagaRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class IdempotencyKeyManager {
    
    private final SagaRedisService sagaRedisService;
    
    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private static final Duration FAILURE_TTL = Duration.ofHours(1);

    public boolean exists(String idempotencyKey) {
        return sagaRedisService.existsIdempotencyKey(idempotencyKey);
    }

    public Optional<IdempotencyRecord> get(String idempotencyKey) {
        return sagaRedisService.getIdempotencyRecord(idempotencyKey);
    }

    public boolean startProcessing(String idempotencyKey, String sagaId) {
        IdempotencyRecord record = IdempotencyRecord.builder()
            .sagaId(sagaId)
            .status(IdempotencyStatus.IN_PROGRESS)
            .createdAt(LocalDateTime.now())
            .expiresAt(LocalDateTime.now().plus(DEFAULT_TTL))
            .build();

        boolean started = sagaRedisService.startProcessing(idempotencyKey, record, DEFAULT_TTL);

        if (!started) {
            log.warn("Duplicate request detected: idempotencyKey={}", idempotencyKey);
        } else {
            log.debug("Idempotency key registered: key={}, sagaId={}", idempotencyKey, sagaId);
        }

        return started;
    }

    public void markCompleted(String idempotencyKey, Object response) {
        Optional<IdempotencyRecord> existingRecord = get(idempotencyKey);
        if (existingRecord.isEmpty()) {
            log.warn("Idempotency record not found for completion: key={}", idempotencyKey);
            return;
        }

        IdempotencyRecord record = IdempotencyRecord.builder()
            .sagaId(existingRecord.get().getSagaId())
            .status(IdempotencyStatus.COMPLETED)
            .response(response)
            .createdAt(existingRecord.get().getCreatedAt())
            .expiresAt(LocalDateTime.now().plus(DEFAULT_TTL))
            .build();

        sagaRedisService.markCompleted(idempotencyKey, record, DEFAULT_TTL);
        log.debug("Idempotency key marked as completed: key={}", idempotencyKey);
    }

    public void markFailed(String idempotencyKey, Exception exception) {
        Optional<IdempotencyRecord> existingRecord = get(idempotencyKey);
        if (existingRecord.isEmpty()) {
            log.warn("Idempotency record not found for failure: key={}", idempotencyKey);
            return;
        }

        IdempotencyRecord record = IdempotencyRecord.builder()
            .sagaId(existingRecord.get().getSagaId())
            .status(IdempotencyStatus.FAILED)
            .response(exception.getMessage())
            .createdAt(existingRecord.get().getCreatedAt())
            .expiresAt(LocalDateTime.now().plus(FAILURE_TTL))
            .build();

        sagaRedisService.markFailed(idempotencyKey, record, FAILURE_TTL);
        log.debug("Idempotency key marked as failed: key={}", idempotencyKey);
    }

    public void delete(String idempotencyKey) {
        sagaRedisService.deleteIdempotencyKey(idempotencyKey);
        log.debug("Idempotency key deleted: key={}", idempotencyKey);
    }
}
