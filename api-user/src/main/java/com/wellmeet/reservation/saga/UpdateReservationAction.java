package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.dto.request.UpdateReservationDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationUpdateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateReservationAction implements SagaAction<String> {

    private final ReservationFeignClient reservationClient;

    @Override
    public String execute(SagaContext context) {
        ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");

        UpdateReservationDTO request = new UpdateReservationDTO(
                ctx.newRestaurantId(),
                ctx.newAvailableDateId(),
                ctx.newPartySize(),
                ctx.specialRequest()
        );

        ReservationDTO updatedReservation = reservationClient.updateReservation(ctx.reservationId(), request);
        context.getData().put("reservation", updatedReservation);

        return updatedReservation.id().toString();
    }
}
