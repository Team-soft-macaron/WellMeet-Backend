package com.wellmeet.reservation.saga;

import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.core.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationConfirmSagaFactory {

    private final ConfirmReservationAction confirmReservationAction;
    private final CancelConfirmationCompensation cancelConfirmationCompensation;
    private final PublishReservationConfirmedAction publishReservationConfirmedAction;

    public SagaDefinition<String> createSaga() {
        return new SagaDefinition<String>("RESERVATION_CONFIRM") {
            {
                addStep(SagaStep.builder()
                        .name("confirmReservation")
                        .forwardAction(confirmReservationAction)
                        .compensationAction(cancelConfirmationCompensation)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());

                addStep(SagaStep.builder()
                        .name("publishEvent")
                        .forwardAction(publishReservationConfirmedAction)
                        .maxRetries(3)
                        .retryDelayMs(1000L)
                        .build());
            }

            @Override
            public String buildResult(SagaContext context) {
                return "confirmed";
            }
        };
    }
}
