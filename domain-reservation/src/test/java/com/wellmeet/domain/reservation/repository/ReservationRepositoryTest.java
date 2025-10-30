package com.wellmeet.domain.reservation.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Nested
    class FindAllByStatusOrderByAvailableDateIdAsc {

        @Test
        void 상태별로_예약을_조회한다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId1 = 1L;
            Long availableDateId2 = 2L;

            createAndSaveReservation(restaurantId, availableDateId1, "member1", ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurantId, availableDateId2, "member2", ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurantId, availableDateId1, "member3", ReservationStatus.PENDING);

            List<Reservation> result = reservationRepository.findAllByStatusOrderByAvailableDateIdAsc(
                    ReservationStatus.CONFIRMED);

            assertThat(result)
                    .hasSize(2)
                    .allMatch(r -> r.getStatus() == ReservationStatus.CONFIRMED);
        }

        @Test
        void 다른_상태의_예약은_조회되지_않는다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId = 1L;

            createAndSaveReservation(restaurantId, availableDateId, "member1", ReservationStatus.PENDING);
            createAndSaveReservation(restaurantId, availableDateId, "member2", ReservationStatus.CANCELED);

            List<Reservation> result = reservationRepository.findAllByStatusOrderByAvailableDateIdAsc(
                    ReservationStatus.CONFIRMED);

            assertThat(result).isEmpty();
        }

        @Test
        void AvailableDateId로_정렬되어_조회된다() {
            String restaurantId = "test-restaurant-id";
            Long availableDateId1 = 1L;
            Long availableDateId2 = 2L;
            Long availableDateId3 = 3L;

            createAndSaveReservation(restaurantId, availableDateId3, "member1", ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurantId, availableDateId1, "member2", ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurantId, availableDateId2, "member3", ReservationStatus.CONFIRMED);

            List<Reservation> result = reservationRepository.findAllByStatusOrderByAvailableDateIdAsc(
                    ReservationStatus.CONFIRMED);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getAvailableDateId()).isEqualTo(1L);
            assertThat(result.get(1).getAvailableDateId()).isEqualTo(2L);
            assertThat(result.get(2).getAvailableDateId()).isEqualTo(3L);
        }
    }

    private Reservation createAndSaveReservation(String restaurantId, Long availableDateId,
                                                 String memberId, ReservationStatus status) {
        Reservation reservation = new Reservation(restaurantId, availableDateId, memberId, 4, "request");
        if (status == ReservationStatus.CONFIRMED) {
            reservation.confirm();
        } else if (status == ReservationStatus.CANCELED) {
            reservation.cancel();
        }
        return reservationRepository.save(reservation);
    }
}
