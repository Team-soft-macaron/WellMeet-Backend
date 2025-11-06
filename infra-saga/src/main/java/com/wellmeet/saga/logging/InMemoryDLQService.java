package com.wellmeet.saga.logging;

import com.wellmeet.saga.core.SagaContext;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class InMemoryDLQService {

    private static final int MAX_SIZE = 100;
    private final ConcurrentLinkedQueue<FailedCompensation> queue = new ConcurrentLinkedQueue<>();

    public void save(String sagaId, String stepName, Throwable error, SagaContext context) {
        FailedCompensation failure = FailedCompensation.builder()
                .sagaId(sagaId)
                .stepName(stepName)
                .errorMessage(error.getMessage())
                .errorDetails(getStackTrace(error))
                .timestamp(LocalDateTime.now())
                .build();

        if (queue.size() >= MAX_SIZE) {
            queue.poll();
        }
        queue.offer(failure);

        log.error("DLQ_SAVED sagaId={} stepName={} error={} queueSize={}", 
                sagaId, stepName, error.getMessage(), queue.size());
    }

    public int getQueueSize() {
        return queue.size();
    }

    public void logAllFailures() {
        queue.forEach(failure -> 
            log.error("DLQ_ENTRY sagaId={} stepName={} error={} timestamp={}", 
                    failure.sagaId, failure.stepName, failure.errorMessage, failure.timestamp)
        );
    }

    private String getStackTrace(Throwable error) {
        StringBuilder sb = new StringBuilder();
        sb.append(error.getClass().getName()).append(": ").append(error.getMessage()).append("\n");
        for (StackTraceElement element : error.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
            if (sb.length() > 2000) {
                sb.append("\n... (truncated)");
                break;
            }
        }
        return sb.toString();
    }

    @Getter
    @Builder
    private static class FailedCompensation {
        private final String sagaId;
        private final String stepName;
        private final String errorMessage;
        private final String errorDetails;
        private final LocalDateTime timestamp;
    }
}
