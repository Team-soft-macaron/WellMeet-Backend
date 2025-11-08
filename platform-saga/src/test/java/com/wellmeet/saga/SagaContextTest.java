package com.wellmeet.saga;

import com.wellmeet.saga.core.SagaContext;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SagaContextTest {
    
    @Nested
    class 컨텍스트_생성 {
        
        @Test
        void Builder로_컨텍스트_생성() {
            SagaContext context = SagaContext.builder()
                .put("memberId", "M123")
                .put("restaurantId", "R456")
                .put("partySize", 4)
                .build();
            
            assertThat((String) context.get("memberId")).isEqualTo("M123");
            assertThat((String) context.get("restaurantId")).isEqualTo("R456");
            assertThat(context.<Integer>get("partySize")).isEqualTo(4);
        }
        
        @Test
        void putAll로_여러_데이터_추가() {
            Map<String, Object> data = Map.of(
                "memberId", "M123",
                "restaurantId", "R456"
            );
            
            SagaContext context = SagaContext.builder()
                .putAll(data)
                .build();
            
            assertThat((String) context.get("memberId")).isEqualTo("M123");
            assertThat((String) context.get("restaurantId")).isEqualTo("R456");
        }
    }
    
    @Nested
    class 컨텍스트_데이터_관리 {
        
        @Test
        void 데이터_추가_및_조회() {
            SagaContext context = SagaContext.builder().build();
            
            context.put("stepResult", "SUCCESS");
            
            assertThat(context.<String>get("stepResult")).isEqualTo("SUCCESS");
        }
        
        @Test
        void getOrDefault로_기본값_조회() {
            SagaContext context = SagaContext.builder().build();
            
            String result = context.getOrDefault("nonExistent", "DEFAULT");
            
            assertThat(result).isEqualTo("DEFAULT");
        }
        
        @Test
        void contains로_키_존재_확인() {
            SagaContext context = SagaContext.builder()
                .put("memberId", "M123")
                .build();
            
            assertThat(context.contains("memberId")).isTrue();
            assertThat(context.contains("nonExistent")).isFalse();
        }
    }
}
