package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CancelReservationAction implements SagaAction<String> {

    private final ReservationFeignClient reservationClient;

    @Override
    public String execute(SagaContext context) {
        ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
        reservationClient.cancelReservation(ctx.reservationId());
        return "reservation_canceled";
    }
}
