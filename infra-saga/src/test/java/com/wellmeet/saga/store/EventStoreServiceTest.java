package com.wellmeet.saga.store;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.saga.store.entity.SagaEventEntity;
import com.wellmeet.saga.store.entity.SagaStatus;
import com.wellmeet.saga.store.entity.SagaStepEntity;
import com.wellmeet.saga.store.entity.StepStatus;
import com.wellmeet.saga.store.repository.SagaEventRepository;
import com.wellmeet.saga.store.repository.SagaStepRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({EventStoreService.class, com.wellmeet.saga.TestSagaApplication.class})
class EventStoreServiceTest {
    
    @Autowired
    private EventStoreService eventStoreService;
    
    @Autowired
    private SagaEventRepository eventRepository;
    
    @Autowired
    private SagaStepRepository stepRepository;
    
    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        stepRepository.deleteAll();
    }
    
    @Nested
    class Saga_이벤트_저장 {
        
        @Test
        void Saga_시작_이벤트_저장() {
            String sagaId = "saga-123";
            String sagaType = "RESERVATION_CREATE";
            Map<String, Object> payload = Map.of("memberId", "M123", "restaurantId", "R456");
            
            eventStoreService.saveSagaStarted(sagaId, sagaType, payload);
            
            List<SagaEventEntity> events = eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId);
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getSagaId()).isEqualTo(sagaId);
            assertThat(events.get(0).getSagaType()).isEqualTo(sagaType);
            assertThat(events.get(0).getEventType()).isEqualTo("SAGA_STARTED");
            assertThat(events.get(0).getStatus()).isEqualTo(SagaStatus.IN_PROGRESS);
        }
        
        @Test
        void Saga_완료_이벤트_저장() {
            String sagaId = "saga-123";
            eventStoreService.saveSagaStarted(sagaId, "RESERVATION_CREATE", Map.of());
            
            eventStoreService.saveSagaCompleted(sagaId);
            
            List<SagaEventEntity> events = eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId);
            assertThat(events).hasSize(2);
            assertThat(events.get(1).getEventType()).isEqualTo("SAGA_COMPLETED");
            assertThat(events.get(1).getStatus()).isEqualTo(SagaStatus.COMPLETED);
        }
    }
    
    @Nested
    class Step_이벤트_저장 {
        
        @Test
        void Step_시작_및_완료() {
            String sagaId = "saga-123";
            String stepName = "GET_MEMBER";
            Map<String, Object> request = Map.of("memberId", "M123");
            Map<String, Object> response = Map.of("name", "홍길동");
            
            eventStoreService.saveStepStarted(sagaId, stepName, 1, "getMember", null, request);
            eventStoreService.saveStepCompleted(sagaId, stepName, response);
            
            List<SagaStepEntity> steps = stepRepository.findBySagaIdOrderByStepOrderAsc(sagaId);
            assertThat(steps).hasSize(1);
            assertThat(steps.get(0).getStepName()).isEqualTo(stepName);
            assertThat(steps.get(0).getStatus()).isEqualTo(StepStatus.SUCCESS);
        }
        
        @Test
        void Step_실패_저장() {
            String sagaId = "saga-123";
            String stepName = "DECREASE_CAPACITY";
            
            eventStoreService.saveStepStarted(sagaId, stepName, 2, "decreaseCapacity", "increaseCapacity", Map.of());
            eventStoreService.saveStepFailed(sagaId, stepName, "Insufficient capacity");
            
            List<SagaStepEntity> steps = stepRepository.findBySagaIdOrderByStepOrderAsc(sagaId);
            assertThat(steps).hasSize(1);
            assertThat(steps.get(0).getStatus()).isEqualTo(StepStatus.FAILED);
        }
    }
    
    @Nested
    class 보상_트랜잭션 {
        
        @Test
        void 완료된_Step_조회_역순() {
            String sagaId = "saga-123";
            
            eventStoreService.saveStepStarted(sagaId, "STEP1", 1, "action1", "comp1", Map.of());
            eventStoreService.saveStepCompleted(sagaId, "STEP1", Map.of());
            
            eventStoreService.saveStepStarted(sagaId, "STEP2", 2, "action2", "comp2", Map.of());
            eventStoreService.saveStepCompleted(sagaId, "STEP2", Map.of());
            
            eventStoreService.saveStepStarted(sagaId, "STEP3", 3, "action3", "comp3", Map.of());
            eventStoreService.saveStepCompleted(sagaId, "STEP3", Map.of());
            
            List<SagaStepEntity> completedSteps = eventStoreService.getCompletedSteps(sagaId);
            
            assertThat(completedSteps).hasSize(3);
            assertThat(completedSteps.get(0).getStepName()).isEqualTo("STEP3");
            assertThat(completedSteps.get(1).getStepName()).isEqualTo("STEP2");
            assertThat(completedSteps.get(2).getStepName()).isEqualTo("STEP1");
        }
        
        @Test
        void Saga_보상_시작_및_완료() {
            String sagaId = "saga-123";
            eventStoreService.saveSagaStarted(sagaId, "RESERVATION_CREATE", Map.of());
            
            eventStoreService.saveSagaCompensating(sagaId);
            eventStoreService.saveSagaCompensated(sagaId);
            
            List<SagaEventEntity> events = eventRepository.findBySagaIdOrderByEventSequenceAsc(sagaId);
            assertThat(events).hasSize(3);
            assertThat(events.get(1).getEventType()).isEqualTo("SAGA_COMPENSATING");
            assertThat(events.get(2).getEventType()).isEqualTo("SAGA_COMPENSATED");
            assertThat(events.get(2).getStatus()).isEqualTo(SagaStatus.COMPENSATED);
        }
    }
}
