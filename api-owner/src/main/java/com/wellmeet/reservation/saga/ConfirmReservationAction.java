package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConfirmReservationAction implements SagaAction<String> {

    private final ReservationFeignClient reservationClient;

    @Override
    public String execute(SagaContext context) {
        ReservationConfirmContext ctx = (ReservationConfirmContext) context.getData().get("confirmContext");
        reservationClient.confirmReservation(ctx.reservationId());

        ReservationDTO reservation = reservationClient.getReservation(ctx.reservationId());
        context.getData().put("reservation", reservation);

        return "reservation_confirmed";
    }
}
