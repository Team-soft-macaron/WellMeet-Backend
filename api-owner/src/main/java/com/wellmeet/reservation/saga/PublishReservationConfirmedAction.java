package com.wellmeet.reservation.saga;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishReservationConfirmedAction implements SagaAction<String> {

    private final MemberFeignClient memberClient;
    private final RestaurantFeignClient restaurantClient;
    private final OwnerEventPublishBffService eventPublishService;

    @Override
    public String execute(SagaContext context) {
        ReservationDTO reservation = (ReservationDTO) context.getData().get("reservation");

        MemberDTO member = memberClient.getMember(reservation.memberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.restaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.restaurantId(),
                reservation.availableDateId()
        );

        LocalDateTime dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                reservation,
                member.name(),
                restaurant.name(),
                dateTime
        );

        eventPublishService.publishReservationConfirmedEvent(event);
        return "event_published";
    }
}
