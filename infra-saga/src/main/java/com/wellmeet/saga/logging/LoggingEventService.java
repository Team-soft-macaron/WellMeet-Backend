package com.wellmeet.saga.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class LoggingEventService {

    public void saveSagaStarted(String sagaId, String sagaType, Map<String, Object> contextData) {
        log.info("SAGA_STARTED sagaId={} sagaType={} data={}", sagaId, sagaType, contextData);
    }

    public void saveSagaCompleted(String sagaId) {
        log.info("SAGA_COMPLETED sagaId={}", sagaId);
    }

    public void saveSagaCompensating(String sagaId) {
        log.warn("SAGA_COMPENSATING sagaId={}", sagaId);
    }

    public void saveSagaCompensated(String sagaId) {
        log.warn("SAGA_COMPENSATED sagaId={}", sagaId);
    }

    public void saveStepStarted(String sagaId, String stepName, int sequence, String sagaType, String description, Map<String, Object> contextData) {
        log.info("STEP_STARTED sagaId={} stepName={} sequence={} sagaType={} description={}", 
                sagaId, stepName, sequence, sagaType, description);
    }

    public void saveStepCompleted(String sagaId, String stepName, Map<String, Object> results) {
        log.info("STEP_COMPLETED sagaId={} stepName={} results={}", sagaId, stepName, results);
    }

    public void saveStepFailed(String sagaId, String stepName, String errorMessage) {
        log.error("STEP_FAILED sagaId={} stepName={} error={}", sagaId, stepName, errorMessage);
    }

    public void saveStepCompensated(String sagaId, String stepName) {
        log.info("STEP_COMPENSATED sagaId={} stepName={}", sagaId, stepName);
    }
}
