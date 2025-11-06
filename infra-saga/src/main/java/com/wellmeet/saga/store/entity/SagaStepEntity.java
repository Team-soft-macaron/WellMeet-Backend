package com.wellmeet.saga.store.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(
    name = "saga_step_store",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_saga_step", columnNames = {"saga_id", "step_name"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SagaStepEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "saga_id", nullable = false)
    private String sagaId;
    
    @Column(name = "step_name", nullable = false)
    private String stepName;
    
    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StepStatus status;
    
    @Column(name = "forward_action", nullable = false)
    private String forwardAction;
    
    @Column(name = "compensation_action")
    private String compensationAction;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "JSON")
    private Map<String, Object> requestPayload;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "JSON")
    private Map<String, Object> responsePayload;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "retries")
    @Builder.Default
    private Integer retries = 0;
    
    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void incrementRetries() {
        this.retries++;
    }

    public boolean canRetry() {
        return this.retries < this.maxRetries;
    }

    public void updateStatus(StepStatus newStatus) {
        this.status = newStatus;
        if (newStatus == StepStatus.SUCCESS || newStatus == StepStatus.COMPENSATED) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public void updateResponsePayload(Map<String, Object> responsePayload) {
        this.responsePayload = responsePayload;
    }

    public void updateErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
