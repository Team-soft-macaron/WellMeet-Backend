package com.wellmeet.global.event;

import static org.mockito.Mockito.verify;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class EventPublishServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EventPublishService eventPublishService;

    @Nested
    class PublishRestaurantUpdatedEvent {

        @Test
        void 식당_업데이트_이벤트를_발행한다() {
            String restaurantId = "restaurant-1";
            RestaurantUpdatedEvent event = new RestaurantUpdatedEvent(restaurantId);

            eventPublishService.publishRestaurantUpdatedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishReservationConfirmedEvent {

        @Test
        void 예약_확정_이벤트를_발행한다() {
            Restaurant restaurant = createRestaurant();
            AvailableDate availableDate = createAvailableDate(restaurant);
            Member member = createMember();
            Reservation reservation = createReservation(restaurant, availableDate, member);
            ReservationConfirmedEvent event = new ReservationConfirmedEvent(reservation, member.getName());

            eventPublishService.publishReservationConfirmedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    private Restaurant createRestaurant() {
        Owner owner = new Owner("owner-name", "owner@email.com");
        return new Restaurant(
                "restaurant-1",
                "Test Restaurant",
                "서울시",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner.getId()
        );
    }

    private AvailableDate createAvailableDate(Restaurant restaurant) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        return new AvailableDate(dateTime.toLocalDate(), dateTime.toLocalTime(), 10, restaurant);
    }

    private Member createMember() {
        return new Member("member", "nickname", "member@email.com", "010-1234-5678");
    }

    private Reservation createReservation(Restaurant restaurant, AvailableDate availableDate, Member member) {
        return new Reservation(restaurant, availableDate, member.getId(), 4, "request");
    }
}
