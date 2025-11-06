package com.wellmeet.saga.idempotency;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.saga.IdempotencyRecord;
import com.wellmeet.saga.IdempotencyStatus;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = com.wellmeet.saga.TestSagaApplication.class)
@ActiveProfiles("test")
class IdempotencyKeyManagerTest {

    @Autowired
    private IdempotencyKeyManager idempotencyKeyManager;

    @BeforeEach
    void setUp() {
        idempotencyKeyManager.delete("member:M123:restaurant:R456:date:789");
        idempotencyKeyManager.delete("reservation:123:update");
        idempotencyKeyManager.delete("reservation:123:cancel");
    }
    
    @Nested
    class 멱등성_키_생성 {
        
        @Test
        void 키_생성_테스트() {
            String key1 = IdempotencyKeyGenerator.forReservationCreate("M123", "R456", 789L);
            String key2 = IdempotencyKeyGenerator.forReservationUpdate(123L);
            String key3 = IdempotencyKeyGenerator.forReservationCancel(123L);
            
            assertThat(key1).isEqualTo("member:M123:restaurant:R456:date:789");
            assertThat(key2).isEqualTo("reservation:123:update");
            assertThat(key3).isEqualTo("reservation:123:cancel");
        }
    }
    
    @Nested
    class 처리_시작 {
        
        @Test
        void 새로운_키로_처리_시작() {
            String key = "member:M123:restaurant:R456:date:789";
            String sagaId = "saga-123";
            
            boolean result = idempotencyKeyManager.startProcessing(key, sagaId);
            
            assertThat(result).isTrue();
            assertThat(idempotencyKeyManager.exists(key)).isTrue();
            
            Optional<IdempotencyRecord> record = idempotencyKeyManager.get(key);
            assertThat(record).isPresent();
            assertThat(record.get().getSagaId()).isEqualTo(sagaId);
            assertThat(record.get().getStatus()).isEqualTo(IdempotencyStatus.IN_PROGRESS);
        }
        
        @Test
        void 중복_키로_처리_시작_실패() {
            String key = "member:M123:restaurant:R456:date:789";
            
            idempotencyKeyManager.startProcessing(key, "saga-1");
            boolean result = idempotencyKeyManager.startProcessing(key, "saga-2");
            
            assertThat(result).isFalse();
        }
    }
    
    @Nested
    class 처리_완료 {
        
        @Test
        void 처리_완료_후_응답_저장() {
            String key = "member:M123:restaurant:R456:date:789";
            String sagaId = "saga-123";
            Object response = Map.of("reservationId", 100L);
            
            idempotencyKeyManager.startProcessing(key, sagaId);
            idempotencyKeyManager.markCompleted(key, response);
            
            Optional<IdempotencyRecord> record = idempotencyKeyManager.get(key);
            assertThat(record).isPresent();
            assertThat(record.get().getStatus()).isEqualTo(IdempotencyStatus.COMPLETED);
            assertThat(record.get().getResponse()).isNotNull();
        }
        
        @Test
        void 완료된_요청에_대한_중복_요청() {
            String key = "member:M123:restaurant:R456:date:789";
            Object response = Map.of("reservationId", 100L);
            
            idempotencyKeyManager.startProcessing(key, "saga-1");
            idempotencyKeyManager.markCompleted(key, response);
            
            Optional<IdempotencyRecord> record = idempotencyKeyManager.get(key);
            assertThat(record).isPresent();
            assertThat(record.get().getStatus()).isEqualTo(IdempotencyStatus.COMPLETED);

            boolean result = idempotencyKeyManager.startProcessing(key, "saga-2");
            assertThat(result).isFalse();
        }
    }
    
    @Nested
    class 처리_실패 {
        
        @Test
        void 처리_실패_후_상태_업데이트() {
            String key = "member:M123:restaurant:R456:date:789";
            String sagaId = "saga-123";
            Exception exception = new IllegalArgumentException("Insufficient capacity");
            
            idempotencyKeyManager.startProcessing(key, sagaId);
            idempotencyKeyManager.markFailed(key, exception);
            
            Optional<IdempotencyRecord> record = idempotencyKeyManager.get(key);
            assertThat(record).isPresent();
            assertThat(record.get().getStatus()).isEqualTo(IdempotencyStatus.FAILED);
            assertThat(record.get().getResponse()).isEqualTo("Insufficient capacity");
        }
    }
    
    @Nested
    class 키_삭제 {
        
        @Test
        void 멱등성_키_삭제() {
            String key = "member:M123:restaurant:R456:date:789";
            
            idempotencyKeyManager.startProcessing(key, "saga-1");
            assertThat(idempotencyKeyManager.exists(key)).isTrue();
            
            idempotencyKeyManager.delete(key);
            assertThat(idempotencyKeyManager.exists(key)).isFalse();
        }
    }
}
