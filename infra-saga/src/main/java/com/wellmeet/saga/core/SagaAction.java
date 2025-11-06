package com.wellmeet.saga.core;

@FunctionalInterface
public interface SagaAction<T> {

    T execute(SagaContext context) throws Exception;
}
