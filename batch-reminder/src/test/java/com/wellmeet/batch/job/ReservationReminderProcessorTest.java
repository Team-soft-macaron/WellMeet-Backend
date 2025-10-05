package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ReservationReminderProcessorTest {

    private final ReservationReminderProcessor processor = new ReservationReminderProcessor();

    @Nested
    class Process {

        @Test
        void Reservation을_ReservationReminderPayload로_변환한다() {
            Member member = mock(Member.class);
            when(member.getId()).thenReturn("member-123");
            when(member.getName()).thenReturn("홍길동");

            Restaurant restaurant = mock(Restaurant.class);
            when(restaurant.getName()).thenReturn("맛집");

            AvailableDate availableDate = mock(AvailableDate.class);
            when(availableDate.getDate()).thenReturn(LocalDate.of(2025, 10, 5));
            when(availableDate.getTime()).thenReturn(LocalTime.of(18, 0));

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(1L);
            when(reservation.getMember()).thenReturn(member);
            when(reservation.getRestaurant()).thenReturn(restaurant);
            when(reservation.getRestaurantName()).thenReturn("맛집");
            when(reservation.getAvailableDate()).thenReturn(availableDate);
            when(reservation.getDateTime()).thenReturn(LocalDateTime.of(2025, 10, 5, 18, 0));
            when(reservation.getPartySize()).thenReturn(4);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result).isNotNull();
            assertThat(result.getReservationId()).isEqualTo(1L);
            assertThat(result.getCustomerId()).isEqualTo("member-123");
            assertThat(result.getCustomerName()).isEqualTo("홍길동");
            assertThat(result.getRestaurantName()).isEqualTo("맛집");
            assertThat(result.getReservationTime()).isEqualTo(LocalDateTime.of(2025, 10, 5, 18, 0));
            assertThat(result.getPartySize()).isEqualTo(4);
        }

        @Test
        void 회원정보와_식당정보가_올바르게_매핑된다() {
            Member member = mock(Member.class);
            when(member.getId()).thenReturn("user-456");
            when(member.getName()).thenReturn("김철수");

            Restaurant restaurant = mock(Restaurant.class);
            when(restaurant.getName()).thenReturn("한식당");

            AvailableDate availableDate = mock(AvailableDate.class);
            when(availableDate.getDate()).thenReturn(LocalDate.of(2025, 10, 6));
            when(availableDate.getTime()).thenReturn(LocalTime.of(12, 30));

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(2L);
            when(reservation.getMember()).thenReturn(member);
            when(reservation.getRestaurant()).thenReturn(restaurant);
            when(reservation.getRestaurantName()).thenReturn("한식당");
            when(reservation.getAvailableDate()).thenReturn(availableDate);
            when(reservation.getDateTime()).thenReturn(LocalDateTime.of(2025, 10, 6, 12, 30));
            when(reservation.getPartySize()).thenReturn(2);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result.getCustomerId()).isEqualTo("user-456");
            assertThat(result.getCustomerName()).isEqualTo("김철수");
            assertThat(result.getRestaurantName()).isEqualTo("한식당");
        }

        @Test
        void 예약_시간이_LocalDateTime으로_올바르게_변환된다() {
            Member member = mock(Member.class);
            when(member.getId()).thenReturn("member-789");
            when(member.getName()).thenReturn("이영희");

            Restaurant restaurant = mock(Restaurant.class);
            when(restaurant.getName()).thenReturn("레스토랑");

            LocalDate date = LocalDate.of(2025, 12, 25);
            LocalTime time = LocalTime.of(19, 30);
            LocalDateTime expectedDateTime = LocalDateTime.of(date, time);

            AvailableDate availableDate = mock(AvailableDate.class);
            when(availableDate.getDate()).thenReturn(date);
            when(availableDate.getTime()).thenReturn(time);

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(3L);
            when(reservation.getMember()).thenReturn(member);
            when(reservation.getRestaurant()).thenReturn(restaurant);
            when(reservation.getRestaurantName()).thenReturn("레스토랑");
            when(reservation.getAvailableDate()).thenReturn(availableDate);
            when(reservation.getDateTime()).thenReturn(expectedDateTime);
            when(reservation.getPartySize()).thenReturn(6);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result.getReservationTime()).isEqualTo(expectedDateTime);
        }
    }
}
