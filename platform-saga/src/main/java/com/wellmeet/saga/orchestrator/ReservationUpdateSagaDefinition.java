package com.wellmeet.saga.orchestrator;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;

public class ReservationUpdateSagaDefinition extends SagaDefinition<String> {

    public ReservationUpdateSagaDefinition() {
        super("RESERVATION_UPDATE");

        addStep(SagaStep.builder()
                .name("increaseOldCapacity")
                .forwardAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("increaseOldCapacity_executed", true);
                    return "old_capacity_restored";
                })
                .compensationAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("decreaseOldCapacity_executed", true);
                    return null;
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());

        addStep(SagaStep.builder()
                .name("decreaseNewCapacity")
                .forwardAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("decreaseNewCapacity_executed", true);
                    return "new_capacity_decreased";
                })
                .compensationAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("increaseNewCapacity_executed", true);
                    return null;
                })
                .maxRetries(3)
                .retryDelayMs(1000L)
                .build());

        addStep(SagaStep.builder()
                .name("updateReservation")
                .forwardAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("updateReservation_executed", true);
                    return "reservation_updated";
                })
                .compensationAction(context -> {
                    ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
                    context.getData().put("rollbackReservation_executed", true);
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
        return "reservation_updated";
    }
}
