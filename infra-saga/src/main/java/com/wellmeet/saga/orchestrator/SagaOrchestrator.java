package com.wellmeet.saga.orchestrator;

import com.wellmeet.saga.compensation.CompensationHandler;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import com.wellmeet.saga.executor.SagaExecutor;
import com.wellmeet.saga.idempotency.IdempotencyKeyManager;
import com.wellmeet.saga.logging.LoggingEventService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaOrchestrator {

    private final SagaExecutor sagaExecutor;
    private final CompensationHandler compensationHandler;
    private final LoggingEventService loggingEventService;
    private final IdempotencyKeyManager idempotencyKeyManager;

    public void execute(SagaDefinition<?> definition, SagaContext context) throws SagaExecutionException {
        String sagaId = context.getSagaId();
        String idempotencyKey = context.getIdempotencyKey();

        checkIdempotency(sagaId, idempotencyKey);

        loggingEventService.saveSagaStarted(sagaId, definition.getSagaType(), context.getData());
        log.info("Starting saga execution: sagaId={}, sagaType={}", sagaId, definition.getSagaType());

        List<SagaStep> steps = definition.getSteps();
        int failedStepIndex = executeSteps(sagaId, definition.getSagaType(), steps, context);

        if (failedStepIndex == -1) {
            handleSuccess(sagaId, idempotencyKey, context);
        } else {
            handleFailure(sagaId, idempotencyKey, steps, context, failedStepIndex);
        }
    }

    private void checkIdempotency(String sagaId, String idempotencyKey) throws SagaExecutionException {
        if (idempotencyKey != null && !idempotencyKeyManager.startProcessing(idempotencyKey, sagaId)) {
            log.warn("Duplicate saga execution detected: sagaId={}, idempotencyKey={}", sagaId, idempotencyKey);
            throw new SagaExecutionException("Duplicate saga execution: " + idempotencyKey);
        }
    }

    private int executeSteps(String sagaId, String sagaType, List<SagaStep> steps, SagaContext context) {
        for (int i = 0; i < steps.size(); i++) {
            SagaStep step = steps.get(i);
            try {
                sagaExecutor.executeStep(sagaId, sagaType, step, context, i + 1);
            } catch (Exception e) {
                log.error("Saga step failed: sagaId={}, stepName={}, error={}", sagaId, step.getName(), e.getMessage());
                return i;
            }
        }
        return -1;
    }

    private void handleSuccess(String sagaId, String idempotencyKey, SagaContext context) {
        loggingEventService.saveSagaCompleted(sagaId);
        log.info("Saga completed successfully: sagaId={}", sagaId);

        if (idempotencyKey != null) {
            idempotencyKeyManager.markCompleted(idempotencyKey, context.getStepResults());
        }
    }

    private void handleFailure(String sagaId, String idempotencyKey, List<SagaStep> steps, SagaContext context, int failedStepIndex) throws SagaExecutionException {
        loggingEventService.saveSagaCompensating(sagaId);
        log.error("Saga failed, starting compensation: sagaId={}, failedStep={}", sagaId, steps.get(failedStepIndex).getName());

        compensationHandler.compensate(sagaId, steps, context, failedStepIndex);

        loggingEventService.saveSagaCompensated(sagaId);
        log.info("Saga compensated: sagaId={}", sagaId);

        if (idempotencyKey != null) {
            idempotencyKeyManager.markFailed(idempotencyKey, new RuntimeException("Saga failed at step: " + steps.get(failedStepIndex).getName()));
        }

        throw new SagaExecutionException("Saga execution failed at step: " + steps.get(failedStepIndex).getName());
    }
}