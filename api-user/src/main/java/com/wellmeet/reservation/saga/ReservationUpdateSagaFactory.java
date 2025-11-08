package com.wellmeet.reservation.saga;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationUpdateSagaFactory {

    private final IncreaseOldCapacityAction increaseOldCapacityAction;
    private final DecreaseOldCapacityCompensation decreaseOldCapacityCompensation;
    private final DecreaseNewCapacityAction decreaseNewCapacityAction;
    private final IncreaseNewCapacityCompensation increaseNewCapacityCompensation;
    private final UpdateReservationAction updateReservationAction;
    private final RestoreOldReservationCompensation restoreOldReservationCompensation;
    private final PublishReservationUpdatedAction publishReservationUpdatedAction;

    public SagaDefinition<String> createSaga() {
        return new SagaDefinition<String>("RESERVATION_UPDATE") {
            {
                addStep(SagaStep.builder()
                        .name("increaseOldCapacity")
                        .forwardAction(increaseOldCapacityAction)
                        .compensationAction(decreaseOldCapacityCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("decreaseNewCapacity")
                        .forwardAction(decreaseNewCapacityAction)
                        .compensationAction(increaseNewCapacityCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("updateReservation")
                        .forwardAction(updateReservationAction)
                        .compensationAction(restoreOldReservationCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("publishEvent")
                        .forwardAction(publishReservationUpdatedAction)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());
            }

            @Override
            public String buildResult(SagaContext context) {
                Long reservationId = (Long) context.getData().get("reservationId");
                return reservationId != null ? reservationId.toString() : null;
            }
        };
    }
}
