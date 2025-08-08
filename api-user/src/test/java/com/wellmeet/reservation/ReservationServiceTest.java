package com.wellmeet.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.wellmeet.BaseServiceTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Nested
    class Reserve {

        @Test
        void 한_사람이_같은_예약_요청을_동시에_여러번_신청해도_한_번만_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1);
            int capacity = 100;
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), capacity,
                    restaurant1);
            int partySize = 4;
            CreateReservationRequest request = new CreateReservationRequest(
                    restaurant1.getId(), availableDate.getId(), partySize, "request"
            );
            Member member = memberGenerator.generate("test");

            runAtSameTime(500, () -> reservationService.reserve(member.getId(), request));
            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate = availableDateRepository.findById(availableDate.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(1),
                    () -> assertThat(foundAvailableDate.getMaxCapacity()).isEqualTo(capacity - partySize)
            );
        }

        @Test
        void 여러_사람이_예약_요청을_동시에_신청해도_적절히_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1);
            int capacity = 100;
            AvailableDate availableDate = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), capacity,
                    restaurant1);
            int partySize = 2;
            CreateReservationRequest request = new CreateReservationRequest(
                    restaurant1.getId(), availableDate.getId(), partySize, "request"
            );
            List<Runnable> tasks = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                Member member = memberGenerator.generate("member" + i);
                tasks.add(() -> reservationService.reserve(member.getId(), request));
            }

            runAtSameTime(tasks);
            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate = availableDateRepository.findById(availableDate.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(50),
                    () -> assertThat(foundAvailableDate.getMaxCapacity()).isZero()
            );
        }
    }
}
