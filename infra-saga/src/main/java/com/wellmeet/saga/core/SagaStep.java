package com.wellmeet.saga.core;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SagaStep {

    private final String name;

    private final SagaAction<?> forwardAction;

    private final SagaAction<?> compensationAction;

    @Builder.Default
    private final int maxRetries = 3;

    @Builder.Default
    private final long retryDelayMs = 1000;

    public boolean hasCompensation() {
        return compensationAction != null;
    }
}
