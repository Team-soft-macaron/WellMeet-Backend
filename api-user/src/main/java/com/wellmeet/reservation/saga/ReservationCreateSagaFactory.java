package com.wellmeet.reservation.saga;

import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCreateSagaFactory {

    private final DecreaseCapacityAction decreaseCapacityAction;
    private final IncreaseCapacityCompensation increaseCapacityCompensation;
    private final CreateReservationAction createReservationAction;
    private final DeleteReservationCompensation deleteReservationCompensation;
    private final PublishReservationCreatedAction publishReservationCreatedAction;

    public SagaDefinition<String> createSaga() {
        return new SagaDefinition<String>("RESERVATION_CREATE") {
            {
                addStep(SagaStep.builder()
                        .name("decreaseCapacity")
                        .forwardAction(decreaseCapacityAction)
                        .compensationAction(increaseCapacityCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("createReservation")
                        .forwardAction(createReservationAction)
                        .compensationAction(deleteReservationCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("publishEvent")
                        .forwardAction(publishReservationCreatedAction)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());
            }

            @Override
            public String buildResult(com.wellmeet.saga.core.SagaContext context) {
                Long reservationId = (Long) context.getData().get("reservationId");
                return reservationId != null ? reservationId.toString() : null;
            }
        };
    }
}
