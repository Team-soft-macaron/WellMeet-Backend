package com.wellmeet.reservation.saga;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncreaseCapacityCompensation implements SagaAction<Void> {

    private final RestaurantAvailableDateFeignClient availableDateClient;

    @Override
    public Void execute(SagaContext context) {
        ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
        availableDateClient.increaseCapacity(new IncreaseCapacityRequest(
                ctx.availableDateId(), 
                ctx.partySize()
        ));
        return null;
    }
}
