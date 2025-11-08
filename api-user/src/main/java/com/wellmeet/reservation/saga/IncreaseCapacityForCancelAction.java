package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncreaseCapacityForCancelAction implements SagaAction<String> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public String execute(SagaContext context) {
        ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
        availableDateClient.increaseCapacity(new IncreaseCapacityRequest(
                ctx.availableDateId(),
                ctx.partySize()
        ));
        return "capacity_increased";
    }
}
