package com.wellmeet.global.event;

import static org.mockito.Mockito.verify;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
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
    class PublishReservationCreatedEvent {

        @Test
        void 예약_생성_이벤트를_발행한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = getRestaurant();
            AvailableDate availableDate = getAvailableDate(restaurant);
            LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
            ReservationCreatedEvent event = new ReservationCreatedEvent(reservation, "member", restaurant.getName(), dateTime);

            eventPublishService.publishReservationCreatedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishReservationUpdatedEvent {

        @Test
        void 예약_수정_이벤트를_발행한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = getRestaurant();
            AvailableDate availableDate = getAvailableDate(restaurant);
            LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
            ReservationUpdatedEvent event = new ReservationUpdatedEvent(reservation, "member", restaurant.getName(), dateTime);

            eventPublishService.publishReservationUpdatedEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    @Nested
    class PublishReservationCanceledEvent {

        @Test
        void 예약_취소_이벤트를_발행한다() {
            Reservation reservation = createReservation();
            Restaurant restaurant = getRestaurant();
            AvailableDate availableDate = getAvailableDate(restaurant);
            LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
            ReservationCanceledEvent event = new ReservationCanceledEvent(reservation, "member", restaurant.getName(), dateTime);

            eventPublishService.publishReservationCanceledEvent(event);

            verify(eventPublisher).publishEvent(event);
        }
    }

    private Restaurant getRestaurant() {
        Owner owner = new Owner("owner", "owner@test.com");
        return new Restaurant(
                "식당",
                "description",
                "서울시 강남구",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner.getId()
        );
    }

    private AvailableDate getAvailableDate(Restaurant restaurant) {
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        return new AvailableDate(
                dateTime.toLocalDate(),
                dateTime.toLocalTime(),
                10,
                restaurant
        );
    }

    private Reservation createReservation() {
        Restaurant restaurant = getRestaurant();
        AvailableDate availableDate = getAvailableDate(restaurant);
        Member member = new Member("member", "nickname", "email@test.com", "010-1234-5678");
        return new Reservation(restaurant.getId(), availableDate.getId(), member.getId(), 4, "요청사항");
    }
}
