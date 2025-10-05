package com.wellmeet.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.member.repository.MemberRepository;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
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

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class Save {

        @Test
        void 예약을_저장한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);

            Reservation reservation = new Reservation(restaurant, availableDate, member, 4, "request");

            Reservation saved = reservationDomainService.save(reservation);

            assertThat(saved.getId()).isNotNull();
            assertThat(saved.getPartySize()).isEqualTo(4);
        }
    }

    @Nested
    class GetByIdAndMemberId {

        @Test
        void 예약을_조회한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);
            Reservation reservation = createAndSaveReservation(restaurant, availableDate, member);

            Reservation result = reservationDomainService.getByIdAndMemberId(
                    reservation.getId(),
                    member.getId()
            );

            assertThat(result.getId()).isEqualTo(reservation.getId());
        }

        @Test
        void 다른_회원의_예약_조회_시_예외가_발생한다() {
            Member member1 = createAndSaveMember();
            Member member2 = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);
            Reservation reservation = createAndSaveReservation(restaurant, availableDate, member1);

            assertThatThrownBy(() -> reservationDomainService.getByIdAndMemberId(
                    reservation.getId(),
                    member2.getId()
            ))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS.getMessage());
        }
    }

    @Nested
    class FindAllByMemberId {

        @Test
        void 회원의_모든_예약을_조회한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            AvailableDate ad1 = createAndSaveAvailableDate(restaurant);
            AvailableDate ad2 = createAndSaveAvailableDate(restaurant);

            createAndSaveReservation(restaurant, ad1, member);
            createAndSaveReservation(restaurant, ad2, member);

            List<Reservation> result = reservationDomainService.findAllByMemberId(member.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        void 예약이_없으면_빈_리스트를_반환한다() {
            Member member = createAndSaveMember();

            List<Reservation> result = reservationDomainService.findAllByMemberId(member.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class AlreadyReserved {

        @Test
        void 이미_예약한_경우_예외가_발생한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);
            createAndSaveReservation(restaurant, availableDate, member);

            assertThatThrownBy(() -> reservationDomainService.alreadyReserved(
                    member.getId(),
                    restaurant.getId(),
                    availableDate.getId()
            ))
                    .isInstanceOf(ReservationException.class)
                    .hasMessageContaining(ReservationErrorCode.ALREADY_RESERVED.getMessage());
        }

        @Test
        void 예약하지_않은_경우_예외가_발생하지_않는다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);

            assertThatCode(() -> reservationDomainService.alreadyReserved(
                    member.getId(),
                    restaurant.getId(),
                    availableDate.getId()
            )).doesNotThrowAnyException();
        }
    }

    @Nested
    class AlreadyUpdated {

        @Test
        void 동일한_정보로_수정하면_true를_반환한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);
            Reservation reservation = createAndSaveReservation(restaurant, availableDate, member);

            boolean result = reservationDomainService.alreadyUpdated(
                    member.getId(),
                    restaurant.getId(),
                    availableDate.getId(),
                    reservation.getPartySize()
            );

            assertThat(result).isTrue();
        }

        @Test
        void 다른_정보로_수정하면_false를_반환한다() {
            Member member = createAndSaveMember();
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant);
            Reservation reservation = createAndSaveReservation(restaurant, availableDate, member);

            boolean result = reservationDomainService.alreadyUpdated(
                    member.getId(),
                    restaurant.getId(),
                    availableDate.getId(),
                    reservation.getPartySize() + 1
            );

            assertThat(result).isFalse();
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

    private AvailableDate createAndSaveAvailableDate(Restaurant restaurant) {
        AvailableDate availableDate = new AvailableDate(
                LocalDate.of(2025, 12, 25),
                LocalTime.of(18, 0),
                10,
                restaurant
        );
        return availableDateRepository.save(availableDate);
    }

    private Reservation createAndSaveReservation(Restaurant restaurant, AvailableDate availableDate, Member member) {
        Reservation reservation = new Reservation(restaurant, availableDate, member, 4, "request");
        return reservationRepository.save(reservation);
    }
}
