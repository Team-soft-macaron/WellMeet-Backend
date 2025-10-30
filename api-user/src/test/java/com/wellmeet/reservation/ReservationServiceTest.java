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
import com.wellmeet.reservation.dto.CreateReservationResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRedisService reservationRedisService;

    @BeforeEach
    void setUp() {
        reservationRedisService.deleteReservationLock();
    }

    @Nested
    class Reserve {

        @Test
        void 한_사람이_같은_예약_요청을_동시에_여러번_신청해도_한_번만_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1.getId());
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
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1.getId());
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

    @Nested
    class UpdateReservation {

        @Test
        void 같은_예약시간의_인원수를_변경할_수_있다() {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1.getId());
            int capacity = 16;
            AvailableDate availableDate1 = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), capacity,
                    restaurant1);
            int partySize = 4;
            Member member1 = memberGenerator.generate("member1");
            CreateReservationRequest createRequest1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate1.getId(), partySize, "request"
            );
            CreateReservationResponse reserve1 = reservationService.reserve(member1.getId(), createRequest1);
            int changePartySize = 7;
            CreateReservationRequest request1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate1.getId(), changePartySize, "request"
            );

            reservationService.updateReservation(
                    reserve1.getId(), member1.getId(), request1
            );
            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate1 = availableDateRepository.findById(availableDate1.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(1),
                    () -> assertThat(foundAvailableDate1.getMaxCapacity()).isEqualTo(capacity - changePartySize)
            );
        }

        @Test
        void 한_사람이_업데이트_요청을_동시에_여러개_보내도_한_번만_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1.getId());
            int capacity = 50;
            AvailableDate availableDate1 = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), capacity,
                    restaurant1);
            AvailableDate availableDate2 = availableDateGenerator.generate(LocalDateTime.now().plusDays(2), capacity,
                    restaurant1);
            int partySize = 4;
            Member member1 = memberGenerator.generate("member1");
            CreateReservationRequest createRequest1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate1.getId(), partySize, "request"
            );
            CreateReservationResponse reserve1 = reservationService.reserve(member1.getId(), createRequest1);
            int changePartySize = 7;
            CreateReservationRequest request1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate2.getId(), changePartySize, "request"
            );

            runAtSameTime(2, () -> reservationService.updateReservation(
                    reserve1.getId(), member1.getId(), request1
            ));
            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate1 = availableDateRepository.findById(availableDate1.getId()).get();
            AvailableDate foundAvailableDate2 = availableDateRepository.findById(availableDate2.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(1),
                    () -> assertThat(foundAvailableDate1.getMaxCapacity()).isEqualTo(capacity),
                    () -> assertThat(foundAvailableDate2.getMaxCapacity()).isEqualTo(capacity - changePartySize)
            );
        }

        @Test
        void 여러_사람이_업데이트_요청을_동시에_여러개_보내도_적절히_처리된다() throws InterruptedException {
            Owner owner1 = ownerGenerator.generate("owner1");
            Restaurant restaurant1 = restaurantGenerator.generate("restaurant1", owner1.getId());
            int capacity = 16;
            AvailableDate availableDate1 = availableDateGenerator.generate(LocalDateTime.now().plusDays(1), capacity,
                    restaurant1);
            AvailableDate availableDate2 = availableDateGenerator.generate(LocalDateTime.now().plusDays(2), capacity,
                    restaurant1);
            AvailableDate availableDate3 = availableDateGenerator.generate(LocalDateTime.now().plusDays(3), capacity,
                    restaurant1);
            int partySize = 4;
            Member member1 = memberGenerator.generate("member1");
            Member member2 = memberGenerator.generate("member2");
            CreateReservationRequest createRequest1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate1.getId(), partySize, "request"
            );
            CreateReservationRequest createRequest2 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate2.getId(), partySize, "request"
            );
            CreateReservationResponse reserve1 = reservationService.reserve(member1.getId(), createRequest1);
            CreateReservationResponse reserve2 = reservationService.reserve(member2.getId(), createRequest2);
            int changePartySize = 7;
            CreateReservationRequest request1 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate3.getId(), changePartySize, "request"
            );
            CreateReservationRequest request2 = new CreateReservationRequest(
                    restaurant1.getId(), availableDate3.getId(), changePartySize, "request"
            );
            List<Runnable> tasks = new ArrayList<>();
            tasks.add(() -> reservationService.updateReservation(
                    reserve1.getId(), member1.getId(), request1
            ));
            tasks.add(() -> reservationService.updateReservation(
                    reserve2.getId(), member2.getId(), request2
            ));

            runAtSameTime(tasks);
            List<Reservation> reservations = reservationRepository.findAll();
            AvailableDate foundAvailableDate1 = availableDateRepository.findById(availableDate1.getId()).get();
            AvailableDate foundAvailableDate2 = availableDateRepository.findById(availableDate2.getId()).get();
            AvailableDate foundAvailableDate3 = availableDateRepository.findById(availableDate3.getId()).get();

            assertAll(
                    () -> assertThat(reservations).hasSize(2),
                    () -> assertThat(foundAvailableDate1.getMaxCapacity()).isEqualTo(capacity),
                    () -> assertThat(foundAvailableDate2.getMaxCapacity()).isEqualTo(capacity),
                    () -> assertThat(foundAvailableDate3.getMaxCapacity()).isEqualTo(capacity - changePartySize * 2)
            );
        }
    }
}
