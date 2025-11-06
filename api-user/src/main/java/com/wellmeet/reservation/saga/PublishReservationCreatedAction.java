package com.wellmeet.reservation.saga;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.global.event.UserEventPublishBffService;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishReservationCreatedAction implements SagaAction<String> {

    private final MemberFeignClient memberClient;
    private final RestaurantFeignClient restaurantClient;
    private final UserEventPublishBffService eventPublishService;

    @Override
    public String execute(SagaContext context) {
        ReservationCreateContext ctx = (ReservationCreateContext) context.getData().get("createContext");
        ReservationDTO reservation = (ReservationDTO) context.getData().get("reservation");
        
        MemberDTO member = memberClient.getMember(ctx.memberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurant(ctx.restaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                ctx.restaurantId(), 
                ctx.availableDateId()
        );
        
        LocalDateTime dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                reservation, 
                member.name(), 
                restaurant.name(), 
                dateTime
        );
        
        eventPublishService.publishReservationCreatedEvent(event);
        return "event_published";
    }
}
