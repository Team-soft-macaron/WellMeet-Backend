package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteReservationCompensation implements SagaAction<Void> {

    private final ReservationFeignClient reservationClient;

    @Override
    public Void execute(SagaContext context) {
        Long reservationId = (Long) context.getData().get("reservationId");
        if (reservationId != null) {
            reservationClient.cancelReservation(reservationId);
        }
        return null;
    }
}
