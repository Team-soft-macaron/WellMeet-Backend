package com.wellmeet.saga.core;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class SagaDefinition<R> {

    private final String sagaType;

    private final List<SagaStep> steps;
    
    protected SagaDefinition(String sagaType) {
        this.sagaType = sagaType;
        this.steps = new ArrayList<>();
    }

    protected void addStep(SagaStep step) {
        this.steps.add(step);
    }

    public List<SagaStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    public abstract R buildResult(SagaContext context);
}
