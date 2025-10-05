package com.wellmeet.batch.job;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ReservationReminderReaderTest {

    @Autowired
    private ReservationReminderReader reader;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    private Member member;
    private Restaurant restaurant;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        availableDateRepository.deleteAll();
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        ownerRepository.deleteAll();

        member = new Member("홍길동", "길동이", "test@example.com", "010-1234-5678");
        memberRepository.save(member);

        Owner owner = new Owner("김사장", "010-9999-9999");
        ownerRepository.save(owner);

        restaurant = new Restaurant("rest-123", "맛집", "서울시 강남구", 37.5, 127.0, "thumbnail.jpg", owner);
        restaurantRepository.save(restaurant);
    }

    @Test
    void 세_시간_후_예약을_조회한다() {
        LocalDateTime threeHoursLater = LocalDateTime.now().plusHours(3).plusMinutes(5);
        AvailableDate availableDate = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime()
        );
        Reservation reservation = createReservation(availableDate, ReservationStatus.CONFIRMED);

        Reservation result = reader.read();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(reservation.getId());
    }

    @Test
    void CONFIRMED_상태_예약만_조회한다() {
        LocalDateTime threeHoursLater = LocalDateTime.now().plusHours(3).plusMinutes(5);
        AvailableDate availableDate1 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime()
        );
        AvailableDate availableDate2 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime().plusMinutes(1)
        );
        AvailableDate availableDate3 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime().plusMinutes(2)
        );

        createReservation(availableDate1, ReservationStatus.PENDING);
        Reservation confirmed = createReservation(availableDate2, ReservationStatus.CONFIRMED);
        createReservation(availableDate3, ReservationStatus.CANCELED);

        Reservation result1 = reader.read();
        Reservation result2 = reader.read();

        assertThat(result1).isNotNull();
        assertThat(result1.getId()).isEqualTo(confirmed.getId());
        assertThat(result2).isNull();
    }

    @Test
    void 시간_윈도우_범위_밖_예약은_조회하지_않는다() {
        LocalDateTime twoHoursLater = LocalDateTime.now().plusHours(2);
        LocalDateTime fourHoursLater = LocalDateTime.now().plusHours(4);

        AvailableDate tooEarly = createAvailableDate(
                twoHoursLater.toLocalDate(),
                twoHoursLater.toLocalTime()
        );
        AvailableDate tooLate = createAvailableDate(
                fourHoursLater.toLocalDate(),
                fourHoursLater.toLocalTime()
        );

        createReservation(tooEarly, ReservationStatus.CONFIRMED);
        createReservation(tooLate, ReservationStatus.CONFIRMED);

        Reservation result = reader.read();

        assertThat(result).isNull();
    }

    @Test
    void 모든_예약_조회_후_null을_반환한다() {
        LocalDateTime threeHoursLater = LocalDateTime.now().plusHours(3).plusMinutes(5);
        AvailableDate availableDate1 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime()
        );
        AvailableDate availableDate2 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime().plusMinutes(1)
        );
        AvailableDate availableDate3 = createAvailableDate(
                threeHoursLater.toLocalDate(),
                threeHoursLater.toLocalTime().plusMinutes(2)
        );

        createReservation(availableDate1, ReservationStatus.CONFIRMED);
        createReservation(availableDate2, ReservationStatus.CONFIRMED);
        createReservation(availableDate3, ReservationStatus.CONFIRMED);

        Reservation result1 = reader.read();
        Reservation result2 = reader.read();
        Reservation result3 = reader.read();
        Reservation result4 = reader.read();

        assertThat(result1).isNotNull();
        assertThat(result2).isNotNull();
        assertThat(result3).isNotNull();
        assertThat(result4).isNull();
    }

    @Test
    void 예약이_없을_때_null을_반환한다() {
        Reservation result = reader.read();

        assertThat(result).isNull();
    }

    private AvailableDate createAvailableDate(LocalDate date, LocalTime time) {
        AvailableDate availableDate = new AvailableDate(date, time, 10, restaurant);
        return availableDateRepository.save(availableDate);
    }

    private Reservation createReservation(AvailableDate availableDate, ReservationStatus status) {
        Reservation reservation = new Reservation(restaurant, availableDate, member, 4, "요청사항");
        if (status == ReservationStatus.CONFIRMED) {
            reservation.confirm();
        } else if (status == ReservationStatus.CANCELED) {
            reservation.cancel();
        }
        return reservationRepository.save(reservation);
    }
}
