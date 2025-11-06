-- Saga Event Store Table (Full Event Sourcing)
CREATE TABLE saga_event_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    saga_id VARCHAR(255) NOT NULL,
    saga_type VARCHAR(100) NOT NULL,
    aggregate_id VARCHAR(255),
    aggregate_type VARCHAR(100),
    
    event_sequence INT NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_payload JSON NOT NULL,
    
    status VARCHAR(50) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    
    INDEX idx_saga_id (saga_id),
    INDEX idx_saga_type (saga_type),
    INDEX idx_aggregate (aggregate_type, aggregate_id),
    INDEX idx_status_created (status, created_at),
    
    CONSTRAINT chk_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'COMPENSATING', 'COMPENSATED', 'FAILED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Saga Step Store Table (Step별 상태 관리)
CREATE TABLE saga_step_store (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    saga_id VARCHAR(255) NOT NULL,
    step_name VARCHAR(100) NOT NULL,
    step_order INT NOT NULL,
    
    status VARCHAR(50) NOT NULL,
    forward_action VARCHAR(255) NOT NULL,
    compensation_action VARCHAR(255),
    
    request_payload JSON NOT NULL,
    response_payload JSON,
    error_message TEXT,
    
    retries INT DEFAULT 0,
    max_retries INT DEFAULT 3,
    
    started_at DATETIME(6),
    completed_at DATETIME(6),
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    
    UNIQUE KEY unique_saga_step (saga_id, step_name),
    INDEX idx_saga_id (saga_id),
    INDEX idx_status (status),
    
    CONSTRAINT chk_step_status CHECK (status IN ('PENDING', 'RUNNING', 'SUCCESS', 'FAILED', 'COMPENSATING', 'COMPENSATED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
