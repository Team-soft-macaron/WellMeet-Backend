package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecreaseCapacityCompensation implements SagaAction<Void> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public Void execute(SagaContext context) {
        ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
        availableDateClient.decreaseCapacity(new DecreaseCapacityRequest(
                ctx.availableDateId(),
                ctx.partySize()
        ));
        return null;
    }
}
