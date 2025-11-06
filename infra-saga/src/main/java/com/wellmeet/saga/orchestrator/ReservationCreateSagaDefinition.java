package com.wellmeet.saga.orchestrator;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;

public class ReservationCreateSagaDefinition extends SagaDefinition<String> {

    public ReservationCreateSagaDefinition() {
        super("RESERVATION_CREATE");

        addStep(SagaStep.builder()
                .name("decreaseCapacity")
                .forwardAction(context -> {
                    ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
                    context.getData().put("decreaseCapacity_executed", true);
                    return "capacity_decreased";
                })
                .compensationAction(context -> {
                    ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
                    context.getData().put("increaseCapacity_executed", true);
                    return null;
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());

        addStep(SagaStep.builder()
                .name("createReservation")
                .forwardAction(context -> {
                    ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
                    String reservationId = "RES-" + System.currentTimeMillis();
                    context.getData().put("reservationId", reservationId);
                    return reservationId;
                })
                .compensationAction(context -> {
                    String reservationId = (String) context.getData().get("reservationId");
                    context.getData().put("deleteReservation_executed", true);
                    return null;
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());

        addStep(SagaStep.builder()
                .name("publishEvent")
                .forwardAction(context -> {
                    context.getData().put("event_published", true);
                    return "event_sent";
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());
    }

    @Override
    public String buildResult(SagaContext context) {
        return (String) context.getData().get("reservationId");
    }
}
