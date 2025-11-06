CREATE TABLE saga_dead_letter_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    saga_id VARCHAR(255) NOT NULL,
    step_name VARCHAR(255) NOT NULL,
    error_message TEXT NOT NULL,
    error_details TEXT,
    retry_count INT NOT NULL DEFAULT 0,
    saga_context TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_saga_id (saga_id),
    INDEX idx_created_at (created_at)
);
