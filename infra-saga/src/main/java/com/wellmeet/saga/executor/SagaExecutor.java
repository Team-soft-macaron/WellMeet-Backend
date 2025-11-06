package com.wellmeet.saga.executor;

import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaStep;
import com.wellmeet.saga.store.EventStoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaExecutor {

    private final EventStoreService eventStoreService;

    public void executeStep(String sagaId, String sagaType, SagaStep step, SagaContext context, int sequence) throws Exception {
        String stepName = step.getName();

        eventStoreService.saveStepStarted(sagaId, stepName, sequence, sagaType, "Executing step: " + stepName, context.getData());
        log.info("Executing step: sagaId={}, stepName={}", sagaId, stepName);

        Exception lastException = executeWithRetry(
            stepName,
            step.getMaxRetries(),
            step.getRetryDelayMs(),
            () -> {
                Object result = step.getForwardAction().execute(context);
                context.setStepResult(stepName, result);
            }
        );

        if (lastException == null) {
            eventStoreService.saveStepCompleted(sagaId, stepName, context.getStepResults());
            log.info("Step completed successfully: sagaId={}, stepName={}", sagaId, stepName);
        } else {
            eventStoreService.saveStepFailed(sagaId, stepName, getErrorMessage(lastException));
            log.error("Step failed after retries: sagaId={}, stepName={}", sagaId, stepName);
            throw lastException;
        }
    }

    public void executeCompensation(String sagaId, SagaStep step, SagaContext context) throws Exception {
        SagaAction<?> compensationAction = step.getCompensationAction();
        if (compensationAction == null) {
            log.info("No compensation action for step: sagaId={}, stepName={}", sagaId, step.getName());
            return;
        }

        String stepName = step.getName();
        log.info("Executing compensation: sagaId={}, stepName={}", sagaId, stepName);

        Exception lastException = executeWithRetry(
            stepName,
            step.getMaxRetries(),
            step.getRetryDelayMs(),
            () -> compensationAction.execute(context)
        );

        if (lastException == null) {
            eventStoreService.saveStepCompensated(sagaId, stepName);
            log.info("Compensation completed successfully: sagaId={}, stepName={}", sagaId, stepName);
        } else {
            eventStoreService.saveStepFailed(sagaId, stepName, "Compensation failed: " + getErrorMessage(lastException));
            log.error("Compensation failed after retries: sagaId={}, stepName={}", sagaId, stepName);
            throw lastException;
        }
    }

    private Exception executeWithRetry(String stepName, int maxRetries, long retryDelayMs, RetryableAction action) {
        Exception lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                if (attempt > 0) {
                    long delay = calculateBackoffDelay(retryDelayMs, attempt);
                    log.warn("Retrying step (attempt {}/{}): stepName={}, delay={}ms",
                            attempt, maxRetries, stepName, delay);
                    sleepInterruptibly(delay);
                }

                action.execute();
                return null;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Step execution interrupted: stepName={}", stepName);
                return e;
            } catch (Exception e) {
                lastException = e;
                log.error("Step execution failed (attempt {}/{}): stepName={}, error={}",
                        attempt, maxRetries, stepName, getErrorMessage(e));

                if (attempt == maxRetries) {
                    break;
                }
            }
        }

        return lastException;
    }

    private long calculateBackoffDelay(long baseDelay, int attempt) {
        return baseDelay * (1L << (attempt - 1));
    }

    private void sleepInterruptibly(long millis) throws InterruptedException {
        Thread.sleep(millis);
    }

    private String getErrorMessage(Throwable error) {
        return error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName();
    }

    @FunctionalInterface
    private interface RetryableAction {
        void execute() throws Exception;
    }
}