package com.wellmeet.saga.store;

import com.wellmeet.saga.store.entity.SagaEventEntity;
import com.wellmeet.saga.store.entity.SagaStatus;
import com.wellmeet.saga.store.entity.SagaStepEntity;
import com.wellmeet.saga.store.entity.StepStatus;
import com.wellmeet.saga.store.repository.SagaEventRepository;
import com.wellmeet.saga.store.repository.SagaStepRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventStoreService {
    
    private final SagaEventRepository eventRepository;
    private final SagaStepRepository stepRepository;

    @Transactional
    public void saveSagaStarted(String sagaId, String sagaType, Map<String, Object> payload) {
        SagaEventEntity event = SagaEventEntity.builder()
            .sagaId(sagaId)
            .sagaType(sagaType)
            .eventSequence(1)
            .eventType("SAGA_STARTED")
            .eventPayload(payload)
            .status(SagaStatus.IN_PROGRESS)
            .build();
        
        eventRepository.save(event);
        log.info("Saga started: sagaId={}, sagaType={}", sagaId, sagaType);
    }

    @Transactional
    public void saveStepStarted(String sagaId, String stepName, int stepOrder, 
                                 String forwardAction, String compensationAction, 
                                 Map<String, Object> requestPayload) {
        SagaStepEntity step = SagaStepEntity.builder()
            .sagaId(sagaId)
            .stepName(stepName)
            .stepOrder(stepOrder)
            .status(StepStatus.RUNNING)
            .forwardAction(forwardAction)
            .compensationAction(compensationAction)
            .requestPayload(requestPayload)
            .build();
        
        stepRepository.save(step);
        log.debug("Step started: sagaId={}, stepName={}", sagaId, stepName);
    }

    @Transactional
    public void saveStepCompleted(String sagaId, String stepName, Map<String, Object> responsePayload) {
        SagaStepEntity step = stepRepository.findBySagaIdAndStepName(sagaId, stepName)
            .orElseThrow(() -> new IllegalStateException("Step not found: " + stepName));
        
        step.updateStatus(StepStatus.SUCCESS);
        step.updateResponsePayload(responsePayload);
        
        stepRepository.save(step);
        log.debug("Step completed: sagaId={}, stepName={}", sagaId, stepName);
    }

    @Transactional
    public void saveStepFailed(String sagaId, String stepName, String errorMessage) {
        SagaStepEntity step = stepRepository.findBySagaIdAndStepName(sagaId, stepName)
            .orElseThrow(() -> new IllegalStateException("Step not found: " + stepName));
        
        step.updateStatus(StepStatus.FAILED);
        step.updateErrorMessage(errorMessage);
        
        stepRepository.save(step);
        log.warn("Step failed: sagaId={}, stepName={}, error={}", sagaId, stepName, errorMessage);
    }

    @Transactional
    public void saveSagaCompleted(String sagaId) {
        int nextSequence = getNextEventSequence(sagaId);
        
        SagaEventEntity event = SagaEventEntity.builder()
            .sagaId(sagaId)
            .sagaType(getSagaType(sagaId))
            .eventSequence(nextSequence)
            .eventType("SAGA_COMPLETED")
            .eventPayload(Map.of("completedAt", System.currentTimeMillis()))
            .status(SagaStatus.COMPLETED)
            .build();
        
        eventRepository.save(event);
        log.info("Saga completed: sagaId={}", sagaId);
    }

    @Transactional
    public void saveSagaCompensating(String sagaId) {
        int nextSequence = getNextEventSequence(sagaId);
        
        SagaEventEntity event = SagaEventEntity.builder()
            .sagaId(sagaId)
            .sagaType(getSagaType(sagaId))
            .eventSequence(nextSequence)
            .eventType("SAGA_COMPENSATING")
            .eventPayload(Map.of("compensatingAt", System.currentTimeMillis()))
            .status(SagaStatus.COMPENSATING)
            .build();
        
        eventRepository.save(event);
        log.warn("Saga compensating: sagaId={}", sagaId);
    }

    @Transactional
    public void saveStepCompensated(String sagaId, String stepName) {
        SagaStepEntity step = stepRepository.findBySagaIdAndStepName(sagaId, stepName)
            .orElseThrow(() -> new IllegalStateException("Step not found: " + stepName));
        
        step.updateStatus(StepStatus.COMPENSATED);
        stepRepository.save(step);
        
        log.debug("Step compensated: sagaId={}, stepName={}", sagaId, stepName);
    }

    @Transactional
    public void saveSagaCompensated(String sagaId) {
        int nextSequence = getNextEventSequence(sagaId);
        
        SagaEventEntity event = SagaEventEntity.builder()
            .sagaId(sagaId)
            .sagaType(getSagaType(sagaId))
            .eventSequence(nextSequence)
            .eventType("SAGA_COMPENSATED")
            .eventPayload(Map.of("compensatedAt", System.currentTimeMillis()))
            .status(SagaStatus.COMPENSATED)
            .build();
        
        eventRepository.save(event);
        log.info("Saga compensated: sagaId={}", sagaId);
    }

    @Transactional
    public void saveCompensationFailed(String sagaId, String stepName, String errorMessage) {
        SagaStepEntity step = stepRepository.findBySagaIdAndStepName(sagaId, stepName)
            .orElseThrow(() -> new IllegalStateException("Step not found: " + stepName));
        
        step.updateStatus(StepStatus.FAILED);
        step.updateErrorMessage("Compensation failed: " + errorMessage);
        
        stepRepository.save(step);
        log.error("Compensation failed: sagaId={}, stepName={}, error={}", sagaId, stepName, errorMessage);
    }

    @Transactional(readOnly = true)
    public List<SagaStepEntity> getCompletedSteps(String sagaId) {
        return stepRepository.findBySagaIdAndStatusOrderByStepOrderDesc(sagaId, StepStatus.SUCCESS);
    }

    @Transactional(readOnly = true)
    public List<SagaEventEntity> getSagaEvents(String sagaId) {
        return eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId);
    }

    private int getNextEventSequence(String sagaId) {
        List<SagaEventEntity> events = eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId);
        return events.isEmpty() ? 1 : events.get(events.size() - 1).getEventSequence() + 1;
    }

    private String getSagaType(String sagaId) {
        return eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId)
            .stream()
            .findFirst()
            .map(SagaEventEntity::getSagaType)
            .orElse("UNKNOWN");
    }
}
