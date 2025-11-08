package com.wellmeet.reservation.saga;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationCancelSagaFactory {

    private final IncreaseCapacityForCancelAction increaseCapacityForCancelAction;
    private final DecreaseCapacityCompensation decreaseCapacityCompensation;
    private final CancelReservationAction cancelReservationAction;
    private final RestoreReservationCompensation restoreReservationCompensation;
    private final PublishReservationCanceledAction publishReservationCanceledAction;

    public SagaDefinition<String> createSaga() {
        return new SagaDefinition<String>("RESERVATION_CANCEL") {
            {
                addStep(SagaStep.builder()
                        .name("increaseCapacity")
                        .forwardAction(increaseCapacityForCancelAction)
                        .compensationAction(decreaseCapacityCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("cancelReservation")
                        .forwardAction(cancelReservationAction)
                        .compensationAction(restoreReservationCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("publishEvent")
                        .forwardAction(publishReservationCanceledAction)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());
            }

            @Override
            public String buildResult(SagaContext context) {
                return "canceled";
            }
        };
    }
}
