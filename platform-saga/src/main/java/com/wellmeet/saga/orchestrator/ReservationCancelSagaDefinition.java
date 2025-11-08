package com.wellmeet.saga.orchestrator;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;

public class ReservationCancelSagaDefinition extends SagaDefinition<String> {

    public ReservationCancelSagaDefinition() {
        super("RESERVATION_CANCEL");

        addStep(SagaStep.builder()
                .name("increaseCapacity")
                .forwardAction(context -> {
                    ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
                    context.getData().put("increaseCapacity_executed", true);
                    return "capacity_restored";
                })
                .compensationAction(context -> {
                    ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
                    context.getData().put("decreaseCapacity_executed", true);
                    return null;
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());

        addStep(SagaStep.builder()
                .name("cancelReservation")
                .forwardAction(context -> {
                    ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
                    context.getData().put("cancelReservation_executed", true);
                    return "reservation_canceled";
                })
                .compensationAction(context -> {
                    ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
                    context.getData().put("revertCancellation_executed", true);
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
        return "reservation_canceled";
    }
}
