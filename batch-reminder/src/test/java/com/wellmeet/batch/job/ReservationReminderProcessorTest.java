package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationReminderProcessorTest {

    @Mock
    private MemberDomainService memberDomainService;

    @Mock
    private RestaurantDomainService restaurantDomainService;

    @Mock
    private Clock clock;

    @InjectMocks
    private ReservationReminderProcessor processor;

    private LocalDateTime fixedNow;

    @BeforeEach
    void setUp() {
        fixedNow = LocalDateTime.of(2025, 10, 5, 15, 0);
        Clock fixedClock = Clock.fixed(
                fixedNow.atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault()
        );
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Nested
    class Process {

        @Test
        void 시간_범위_내의_예약을_올바르게_변환한다() {
            LocalDateTime reservationTime = fixedNow.plusHours(3).plusMinutes(5);
            Member member = new Member("홍길동", "nick", "test@example.com", "010-1234-5678");
            Restaurant restaurant = new Restaurant("rest-1", "맛집", "서울", 37.5, 127.0, "thumb.jpg", "owner-1");
            AvailableDate availableDate = new AvailableDate(
                    reservationTime.toLocalDate(),
                    reservationTime.toLocalTime(),
                    10,
                    restaurant
            );
            Reservation reservation = new Reservation("rest-1", 1L, "member-1", 4, "요청사항");

            when(restaurantDomainService.getAvailableDate(1L, "rest-1")).thenReturn(availableDate);
            when(memberDomainService.getById("member-1")).thenReturn(member);
            when(restaurantDomainService.getById("rest-1")).thenReturn(restaurant);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result).isNotNull();
            assertThat(result.getCustomerId()).isEqualTo("member-1");
            assertThat(result.getCustomerName()).isEqualTo("홍길동");
            assertThat(result.getRestaurantName()).isEqualTo("맛집");
            assertThat(result.getReservationTime()).isEqualTo(reservationTime);
            assertThat(result.getPartySize()).isEqualTo(4);
        }

        @Test
        void 시간_범위_밖의_예약은_null을_반환한다() {
            LocalDateTime reservationTime = fixedNow.plusHours(5);
            AvailableDate availableDate = new AvailableDate(
                    reservationTime.toLocalDate(),
                    reservationTime.toLocalTime(),
                    10,
                    new Restaurant("rest-1", "맛집", "서울", 37.5, 127.0, "thumb.jpg", "owner-1")
            );
            Reservation reservation = new Reservation("rest-1", 1L, "member-1", 4, "요청사항");

            when(restaurantDomainService.getAvailableDate(1L, "rest-1")).thenReturn(availableDate);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result).isNull();
        }
    }
}
