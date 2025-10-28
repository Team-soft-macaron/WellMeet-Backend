package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.kafka.dto.payload.ReservationReminderPayload;
import java.time.LocalDateTime;
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

    @InjectMocks
    private ReservationReminderProcessor processor;

    @Nested
    class Process {

        @Test
        void Reservation을_ReservationReminderPayload로_변환한다() {
            Member member = mock(Member.class);
            when(member.getName()).thenReturn("홍길동");

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(1L);
            when(reservation.getMemberId()).thenReturn("member-123");
            when(reservation.getRestaurantName()).thenReturn("맛집");
            when(reservation.getDateTime()).thenReturn(LocalDateTime.of(2025, 10, 5, 18, 0));
            when(reservation.getPartySize()).thenReturn(4);

            when(memberDomainService.getById("member-123")).thenReturn(member);

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
            when(member.getName()).thenReturn("김철수");

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(2L);
            when(reservation.getMemberId()).thenReturn("user-456");
            when(reservation.getRestaurantName()).thenReturn("한식당");
            when(reservation.getDateTime()).thenReturn(LocalDateTime.of(2025, 10, 6, 12, 30));
            when(reservation.getPartySize()).thenReturn(2);

            when(memberDomainService.getById("user-456")).thenReturn(member);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result.getCustomerId()).isEqualTo("user-456");
            assertThat(result.getCustomerName()).isEqualTo("김철수");
            assertThat(result.getRestaurantName()).isEqualTo("한식당");
        }

        @Test
        void 예약_시간이_LocalDateTime으로_올바르게_변환된다() {
            Member member = mock(Member.class);
            when(member.getName()).thenReturn("이영희");

            LocalDateTime expectedDateTime = LocalDateTime.of(2025, 12, 25, 19, 30);

            Reservation reservation = mock(Reservation.class);
            when(reservation.getId()).thenReturn(3L);
            when(reservation.getMemberId()).thenReturn("member-789");
            when(reservation.getRestaurantName()).thenReturn("레스토랑");
            when(reservation.getDateTime()).thenReturn(expectedDateTime);
            when(reservation.getPartySize()).thenReturn(6);

            when(memberDomainService.getById("member-789")).thenReturn(member);

            ReservationReminderPayload result = processor.process(reservation);

            assertThat(result.getReservationTime()).isEqualTo(expectedDateTime);
        }
    }
}
