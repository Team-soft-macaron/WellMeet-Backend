package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationUpdateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecreaseNewCapacityAction implements SagaAction<String> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public String execute(SagaContext context) {
        ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
        availableDateClient.decreaseCapacity(new DecreaseCapacityRequest(
                ctx.newAvailableDateId(),
                ctx.newPartySize()
        ));
        return "new_capacity_decreased";
    }
}
