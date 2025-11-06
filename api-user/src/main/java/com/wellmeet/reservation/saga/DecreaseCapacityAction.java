package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecreaseCapacityAction implements SagaAction<String> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public String execute(SagaContext context) {
        ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
        availableDateClient.decreaseCapacity(new DecreaseCapacityRequest(
                ctx.availableDateId(), 
                ctx.partySize()
        ));
        return "capacity_decreased";
    }
}
