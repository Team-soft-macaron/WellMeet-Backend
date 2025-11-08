package com.wellmeet.reservation.saga;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.global.event.UserEventPublishBffService;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishReservationCanceledAction implements SagaAction<String> {

    private final MemberFeignClient memberClient;
    private final RestaurantFeignClient restaurantClient;
    private final UserEventPublishBffService eventPublishService;

    @Override
    public String execute(SagaContext context) {
        ReservationCancelContext ctx = (ReservationCancelContext) context.getData().get("cancelContext");
        ReservationDTO reservation = (ReservationDTO) context.getData().get("reservation");

        MemberDTO member = memberClient.getMember(ctx.memberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurant(ctx.restaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                ctx.restaurantId(),
                ctx.availableDateId()
        );

        LocalDateTime dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        ReservationCanceledEvent event = new ReservationCanceledEvent(
                reservation,
                member.name(),
                restaurant.name(),
                dateTime
        );

        eventPublishService.publishReservationCanceledEvent(event);
        return "event_published";
    }
}
