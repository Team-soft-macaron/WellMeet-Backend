package com.wellmeet.domain.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

class ReservationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class FindReservationsForReminderPage {

        @Test
        void 지정한_시간_범위_내의_승인된_예약을_조회한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            LocalDate targetDate = LocalDate.of(2025, 12, 25);
            LocalTime time1 = LocalTime.of(18, 0);
            LocalTime time2 = LocalTime.of(19, 0);
            LocalTime time3 = LocalTime.of(20, 0);

            AvailableDate ad1 = createAndSaveAvailableDate(restaurant, targetDate, time1);
            AvailableDate ad2 = createAndSaveAvailableDate(restaurant, targetDate, time2);
            AvailableDate ad3 = createAndSaveAvailableDate(restaurant, targetDate, time3);

            createAndSaveReservation(restaurant, ad1, member, ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurant, ad2, member, ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurant, ad3, member, ReservationStatus.CONFIRMED);

            LocalDate startDate = LocalDate.of(2025, 12, 25);
            LocalTime startTime = LocalTime.of(18, 30);
            LocalDate endDate = LocalDate.of(2025, 12, 25);
            LocalTime endTime = LocalTime.of(19, 30);

            Page<Reservation> result = reservationRepository.findReservationsForReminderPage(
                    ReservationStatus.CONFIRMED,
                    startDate, startTime,
                    endDate, endTime,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getAvailableDate().getTime()).isEqualTo(time2);
        }

        @Test
        void 승인되지_않은_예약은_조회되지_않는다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            LocalDate targetDate = LocalDate.of(2025, 12, 25);
            LocalTime targetTime = LocalTime.of(19, 0);

            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, targetDate, targetTime);
            createAndSaveReservation(restaurant, availableDate, member, ReservationStatus.PENDING);

            LocalDate startDate = LocalDate.of(2025, 12, 25);
            LocalTime startTime = LocalTime.of(18, 0);
            LocalDate endDate = LocalDate.of(2025, 12, 25);
            LocalTime endTime = LocalTime.of(20, 0);

            Page<Reservation> result = reservationRepository.findReservationsForReminderPage(
                    ReservationStatus.CONFIRMED,
                    startDate, startTime,
                    endDate, endTime,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        void 시간_범위_밖의_예약은_조회되지_않는다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            LocalDate targetDate = LocalDate.of(2025, 12, 25);
            LocalTime time1 = LocalTime.of(17, 0);
            LocalTime time2 = LocalTime.of(21, 0);

            AvailableDate ad1 = createAndSaveAvailableDate(restaurant, targetDate, time1);
            AvailableDate ad2 = createAndSaveAvailableDate(restaurant, targetDate, time2);

            createAndSaveReservation(restaurant, ad1, member, ReservationStatus.CONFIRMED);
            createAndSaveReservation(restaurant, ad2, member, ReservationStatus.CONFIRMED);

            LocalDate startDate = LocalDate.of(2025, 12, 25);
            LocalTime startTime = LocalTime.of(18, 0);
            LocalDate endDate = LocalDate.of(2025, 12, 25);
            LocalTime endTime = LocalTime.of(20, 0);

            Page<Reservation> result = reservationRepository.findReservationsForReminderPage(
                    ReservationStatus.CONFIRMED,
                    startDate, startTime,
                    endDate, endTime,
                    PageRequest.of(0, 10)
            );

            assertThat(result.getContent()).isEmpty();
        }

        @Test
        void 페이징이_정상적으로_동작한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            LocalDate targetDate = LocalDate.of(2025, 12, 25);

            for (int hour = 18; hour <= 20; hour++) {
                AvailableDate ad = createAndSaveAvailableDate(restaurant, targetDate, LocalTime.of(hour, 0));
                createAndSaveReservation(restaurant, ad, member, ReservationStatus.CONFIRMED);
            }

            Page<Reservation> page1 = reservationRepository.findReservationsForReminderPage(
                    ReservationStatus.CONFIRMED,
                    LocalDate.of(2025, 12, 25), LocalTime.of(0, 0),
                    LocalDate.of(2025, 12, 26), LocalTime.of(0, 0),
                    PageRequest.of(0, 2)
            );

            assertThat(page1.getContent()).hasSize(2);
            assertThat(page1.getTotalElements()).isEqualTo(3);
            assertThat(page1.getTotalPages()).isEqualTo(2);
        }
    }

    private Member createAndSaveMember() {
        Member member = new Member("member", "nickname", "email@test.com", "010-1234-5678");
        return memberRepository.save(member);
    }

    private Owner createAndSaveOwner() {
        Owner owner = new Owner("owner", "owner@test.com");
        return ownerRepository.save(owner);
    }

    private Restaurant createAndSaveRestaurant(Owner owner) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID().toString(),
                "식당",
                "address",
                37.5,
                127.0,
                "thumbnail",
                owner
        );
        return restaurantRepository.save(restaurant);
    }

    private AvailableDate createAndSaveAvailableDate(Restaurant restaurant, LocalDate date, LocalTime time) {
        AvailableDate availableDate = new AvailableDate(date, time, 10, restaurant);
        return availableDateRepository.save(availableDate);
    }

    private Reservation createAndSaveReservation(Restaurant restaurant, AvailableDate availableDate,
                                                  Member member, ReservationStatus status) {
        Reservation reservation = new Reservation(restaurant, availableDate, member, 4, "request");
        if (status == ReservationStatus.CONFIRMED) {
            reservation.confirm();
        } else if (status == ReservationStatus.CANCELED) {
            reservation.cancel();
        }
        return reservationRepository.save(reservation);
    }
}
