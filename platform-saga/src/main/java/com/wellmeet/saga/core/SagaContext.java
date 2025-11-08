package com.wellmeet.saga.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SagaContext {
    
    private final String sagaId;
    private final String idempotencyKey;
    private final Map<String, Object> data;
    
    private SagaContext(String sagaId, String idempotencyKey, Map<String, Object> data) {
        this.sagaId = sagaId;
        this.idempotencyKey = idempotencyKey;
        this.data = new HashMap<>(data);
    }

    public void put(String key, Object value) {
        this.data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) this.data.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) this.data.getOrDefault(key, defaultValue);
    }

    public boolean contains(String key) {
        return this.data.containsKey(key);
    }

    public void setStepResult(String stepName, Object result) {
        this.data.put("step_result_" + stepName, result);
    }

    public Object getStepResult(String stepName) {
        return this.data.get("step_result_" + stepName);
    }

    public Map<String, Object> getStepResults() {
        Map<String, Object> results = new HashMap<>();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey().startsWith("step_result_")) {
                results.put(entry.getKey().substring("step_result_".length()), entry.getValue());
            }
        }
        return results;
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String sagaId;
        private String idempotencyKey;
        private final Map<String, Object> data = new HashMap<>();
        
        public Builder sagaId(String sagaId) {
            this.sagaId = sagaId;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder put(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
        
        public Builder putAll(Map<String, Object> data) {
            this.data.putAll(data);
            return this;
        }
        
        public SagaContext build() {
            return new SagaContext(sagaId, idempotencyKey, data);
        }
    }
}
