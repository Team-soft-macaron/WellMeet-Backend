package com.wellmeet.saga.dlq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellmeet.saga.core.SagaContext;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeadLetterQueueService {

    private final DeadLetterQueueRepository repository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void save(String sagaId, String stepName, Throwable error, SagaContext context) {
        String sagaContextJson = serializeContext(context);
        String errorDetails = getStackTrace(error);

        DeadLetterQueueEntity entity = DeadLetterQueueEntity.builder()
                .sagaId(sagaId)
                .stepName(stepName)
                .errorMessage(error.getMessage())
                .errorDetails(errorDetails)
                .retryCount(0)
                .sagaContext(sagaContextJson)
                .build();

        repository.save(entity);

        log.error("Saga step failed and saved to DLQ: sagaId={}, stepName={}, error={}",
                sagaId, stepName, error.getMessage());
    }

    @Transactional(readOnly = true)
    public List<DeadLetterQueueEntity> findBySagaId(String sagaId) {
        return repository.findBySagaIdOrderByCreatedAtDesc(sagaId);
    }

    @Transactional(readOnly = true)
    public List<DeadLetterQueueEntity> findRecentFailures(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return repository.findByCreatedAtAfterOrderByCreatedAtDesc(since);
    }

    @Transactional(readOnly = true)
    public List<DeadLetterQueueEntity> findRetryableSagas(int maxRetryCount) {
        return repository.findByRetryCountLessThanOrderByCreatedAtAsc(maxRetryCount);
    }

    @Transactional
    public void incrementRetryCount(Long id) {
        repository.findById(id).ifPresent(entity -> {
            entity.incrementRetryCount();
            repository.save(entity);
        });
    }

    private String serializeContext(SagaContext context) {
        try {
            return objectMapper.writeValueAsString(context);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize SagaContext", e);
            return "{}";
        }
    }

    private String getStackTrace(Throwable error) {
        StringBuilder sb = new StringBuilder();
        sb.append(error.getClass().getName()).append(": ").append(error.getMessage()).append("\n");
        for (StackTraceElement element : error.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 4000) {
                sb.append("\n... (truncated)");
                break;
            }
        }
        return sb.toString();
    }
}
