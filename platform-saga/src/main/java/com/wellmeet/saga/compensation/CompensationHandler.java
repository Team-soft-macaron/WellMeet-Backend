package com.wellmeet.saga.compensation;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaStep;
import com.wellmeet.saga.logging.InMemoryDLQService;
import com.wellmeet.saga.executor.SagaExecutor;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompensationHandler {

    private final SagaExecutor sagaExecutor;
    private final InMemoryDLQService inMemoryDLQService;

    public void compensate(String sagaId, List<SagaStep> steps, SagaContext context, int failedStepIndex) {
        log.info("Starting compensation: sagaId={}, failedStepIndex={}", sagaId, failedStepIndex);

        for (int i = failedStepIndex - 1; i >= 0; i--) {
            SagaStep step = steps.get(i);

            if (step.getCompensationAction() == null) {
                log.info("Skipping compensation (no action): sagaId={}, stepName={}", sagaId, step.getName());
                continue;
            }

            try {
                sagaExecutor.executeCompensation(sagaId, step, context);
            } catch (Exception e) {
                log.error("Compensation failed, saving to DLQ: sagaId={}, stepName={}", sagaId, step.getName(), e);
                inMemoryDLQService.save(sagaId, step.getName(), e, context);
            }
        }

        log.info("Compensation completed: sagaId={}", sagaId);
    }
}