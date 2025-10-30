package com.wellmeet.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.BaseRepositoryTest;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import com.wellmeet.domain.reservation.repository.ReservationRepository;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(ReservationDomainService.class)
class ReservationDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private ReservationDomainService reservationDomainService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    class Save {

        @Test
        void 예약을_저장한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;

            Reservation reservation = new Reservation(restaurantId, availableDateId, "member", 4, "request");

            Reservation saved = reservationDomainService.save(reservation);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getPartySize()).isEqualTo(4);
        }
    }

    @Nested
    class GetByIdAndMemberId {

        @Test
        void 예약을_조회한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;
            Reservation reservation = createAndSaveReservation(restaurantId, availableDateId, "member");

            Reservation result = reservationDomainService.getByIdAndMemberId(
                    reservation.getId(),
                    "member"
            );

            assertThat(result.getId()).isEqualTo(reservation.getId());
        }

        @Test
        void 다른_회원의_예약_조회_시_예외가_발생한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;
            Reservation reservation = createAndSaveReservation(restaurantId, availableDateId, "member1");

            assertThatThrownBy(() -> reservationDomainService.getByIdAndMemberId(
                    reservation.getId(),
                    "member2"
            ))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS.getMessage());
        }
    }

    @Nested
    class FindAllByMemberId {

        @Test
        void 회원의_모든_예약을_조회한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId1 = 1L;
            Long availableDateId2 = 2L;

            createAndSaveReservation(restaurantId, availableDateId1, "member");
            createAndSaveReservation(restaurantId, availableDateId2, "member");

            List<Reservation> result = reservationDomainService.findAllByMemberId("member");

            assertThat(result).hasSize(2);
        }

        @Test
        void 예약이_없으면_빈_리스트를_반환한다() {

            List<Reservation> result = reservationDomainService.findAllByMemberId("member");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class AlreadyReserved {

        @Test
        void 이미_예약한_경우_예외가_발생한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;
            createAndSaveReservation(restaurantId, availableDateId, "member");

            assertThatThrownBy(() -> reservationDomainService.alreadyReserved(
                    "member",
                    restaurantId,
                    availableDateId
            ))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining(ReservationErrorCode.ALREADY_RESERVED.getMessage());
        }

        @Test
        void 예약하지_않은_경우_예외가_발생하지_않는다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;

            assertThatCode(() -> reservationDomainService.alreadyReserved(
                    "member",
                    restaurantId,
                    availableDateId
            )).doesNotThrowAnyException();
        }
    }

    @Nested
    class AlreadyUpdated {

        @Test
        void 동일한_정보로_수정하면_true를_반환한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;
            Reservation reservation = createAndSaveReservation(restaurantId, availableDateId, "member");

            boolean result = reservationDomainService.alreadyUpdated(
                    "member",
                    restaurantId,
                    availableDateId,
                    reservation.getPartySize()
            );

            assertThat(result).isTrue();
        }

        @Test
        void 다른_정보로_수정하면_false를_반환한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;
            Reservation reservation = createAndSaveReservation(restaurantId, availableDateId, "member");

            boolean result = reservationDomainService.alreadyUpdated(
                    "member",
                    restaurantId,
                    availableDateId,
                    reservation.getPartySize() + 1
            );

            assertThat(result).isFalse();
        }
    }

    private Reservation createAndSaveReservation(String restaurantId, Long availableDateId, String memberId) {
        Reservation reservation = new Reservation(restaurantId, availableDateId, memberId, 4, "request");
        return reservationRepository.save(reservation);
    }
}
