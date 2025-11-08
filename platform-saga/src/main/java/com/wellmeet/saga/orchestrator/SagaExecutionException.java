package com.wellmeet.saga.orchestrator;

public class SagaExecutionException extends Exception {

    public SagaExecutionException(String message) {
        super(message);
    }

    public SagaExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
