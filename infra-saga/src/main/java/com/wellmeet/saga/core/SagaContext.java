package com.wellmeet.saga.core;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class SagaContext {
    
    private final Map<String, Object> data;
    
    private SagaContext(Map<String, Object> data) {
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

    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private final Map<String, Object> data = new HashMap<>();
        
        public Builder put(String key, Object value) {
            this.data.put(key, value);
            return this;
        }
        
        public Builder putAll(Map<String, Object> data) {
            this.data.putAll(data);
            return this;
        }
        
        public SagaContext build() {
            return new SagaContext(data);
        }
    }
}
