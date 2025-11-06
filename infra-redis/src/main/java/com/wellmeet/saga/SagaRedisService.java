package com.wellmeet.saga;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaRedisService {

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "idempotency:";

    public boolean existsIdempotencyKey(String key) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        return bucket.isExists();
    }

    public Optional<IdempotencyRecord> getIdempotencyRecord(String key) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        String value = bucket.get();
        if (value == null) {
            return Optional.empty();
        }

        try {
            IdempotencyRecord record = objectMapper.readValue(value, IdempotencyRecord.class);
            return Optional.of(record);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize IdempotencyRecord: key={}", key, e);
            return Optional.empty();
        }
    }

    public boolean startProcessing(String key, IdempotencyRecord record, Duration ttl) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        if (bucket.isExists()) {
            log.warn("Duplicate request detected: idempotencyKey={}", key);
            return false;
        }

        try {
            String value = objectMapper.writeValueAsString(record);
            bucket.set(value, ttl);
            log.debug("Idempotency key registered: key={}, sagaId={}", key, record.getSagaId());
            return true;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize IdempotencyRecord: key={}", key, e);
            return false;
        }
    }

    public void markCompleted(String key, IdempotencyRecord record, Duration ttl) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        try {
            String value = objectMapper.writeValueAsString(record);
            bucket.set(value, ttl);
            log.debug("Idempotency key marked as completed: key={}", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize completed IdempotencyRecord: key={}", key, e);
        }
    }

    public void markFailed(String key, IdempotencyRecord record, Duration ttl) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);

        try {
            String value = objectMapper.writeValueAsString(record);
            bucket.set(value, ttl);
            log.debug("Idempotency key marked as failed: key={}", key);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize failed IdempotencyRecord: key={}", key, e);
        }
    }

    public void deleteIdempotencyKey(String key) {
        String redisKey = buildKey(key);
        RBucket<String> bucket = redissonClient.getBucket(redisKey);
        bucket.delete();
        log.debug("Idempotency key deleted: key={}", key);
    }

    private String buildKey(String idempotencyKey) {
        return KEY_PREFIX + idempotencyKey;
    }
}
