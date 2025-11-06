package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.request.CreateReservationDTO;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateReservationAction implements SagaAction<String> {

    private final ReservationFeignClient reservationClient;

    @Override
    public String execute(SagaContext context) {
        ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
        
        CreateReservationDTO request = new CreateReservationDTO(
                ctx.restaurantId(),
                ctx.availableDateId(),
                ctx.memberId(),
                ctx.partySize(),
                ctx.specialRequest()
        );
        
        ReservationDTO savedReservation = reservationClient.createReservation(request);
        context.getData().put("reservationId", savedReservation.id());
        context.getData().put("reservation", savedReservation);
        
        return savedReservation.id().toString();
    }
}
