package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationUpdateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncreaseOldCapacityAction implements SagaAction<String> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public String execute(SagaContext context) {
        ReservationUpdateContext ctx = (ReservationUpdateContext) context.getData().get("updateContext");
        availableDateClient.increaseCapacity(new IncreaseCapacityRequest(
                ctx.oldAvailableDateId(),
                ctx.oldPartySize()
        ));
        return "old_capacity_increased";
    }
}
