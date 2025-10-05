package com.wellmeet.domain.restaurant.availabledate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import(AvailableDateDomainService.class)
class AvailableDateDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private AvailableDateDomainService availableDateDomainService;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    class GetAvailableDatesByRestaurantId {

        @Test
        void 식당의_모든_예약_가능_날짜를_조회한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            createAndSaveAvailableDate(restaurant, LocalDate.of(2025, 12, 25), LocalTime.of(18, 0));
            createAndSaveAvailableDate(restaurant, LocalDate.of(2025, 12, 25), LocalTime.of(19, 0));

            List<AvailableDate> result = availableDateDomainService
                    .getAvailableDatesByRestaurantId(restaurant.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        void 예약_가능_날짜가_없으면_빈_리스트를_반환한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            List<AvailableDate> result = availableDateDomainService
                    .getAvailableDatesByRestaurantId(restaurant.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class GetByIdAndRestaurantId {

        @Test
        void 예약_가능_날짜를_조회한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(
                    restaurant,
                    LocalDate.of(2025, 12, 25),
                    LocalTime.of(18, 0)
            );

            AvailableDate result = availableDateDomainService
                    .getByIdAndRestaurantId(availableDate.getId(), restaurant.getId());

            assertThat(result.getId()).isEqualTo(availableDate.getId());
        }

        @Test
        void 존재하지_않는_예약_가능_날짜_조회_시_예외가_발생한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);

            assertThatThrownBy(() -> availableDateDomainService.getByIdAndRestaurantId(999L, restaurant.getId()))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessageContaining(RestaurantErrorCode.AVAILABLE_DATE_NOT_FOUND.getMessage());
        }

        @Test
        void 다른_식당의_예약_가능_날짜_조회_시_예외가_발생한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant1 = createAndSaveRestaurant(owner);
            Restaurant restaurant2 = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(
                    restaurant1,
                    LocalDate.of(2025, 12, 25),
                    LocalTime.of(18, 0)
            );

            assertThatThrownBy(() -> availableDateDomainService
                    .getByIdAndRestaurantId(availableDate.getId(), restaurant2.getId()))
                    .isInstanceOf(RestaurantException.class);
        }
    }

    @Nested
    class DecreaseCapacity {

        @Test
        void 수용_인원을_감소시킨다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(
                    restaurant,
                    LocalDate.of(2025, 12, 25),
                    LocalTime.of(18, 0),
                    10
            );

            availableDateDomainService.decreaseCapacity(availableDate, 4);
            entityManager.flush();
            entityManager.clear();

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(6);
        }

        @Test
        void 수용_인원이_부족하면_예외가_발생한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(
                    restaurant,
                    LocalDate.of(2025, 12, 25),
                    LocalTime.of(18, 0),
                    2
            );

            assertThatThrownBy(() -> availableDateDomainService.decreaseCapacity(availableDate, 4))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessageContaining(RestaurantErrorCode.NOT_ENOUGH_CAPACITY.getMessage());
        }
    }

    @Nested
    class IncreaseCapacity {

        @Test
        void 수용_인원을_증가시킨다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(
                    restaurant,
                    LocalDate.of(2025, 12, 25),
                    LocalTime.of(18, 0),
                    5
            );

            availableDateDomainService.increaseCapacity(availableDate, 3);
            entityManager.flush();
            entityManager.clear();

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(8);
        }
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
        return createAndSaveAvailableDate(restaurant, date, time, 10);
    }

    private AvailableDate createAndSaveAvailableDate(Restaurant restaurant, LocalDate date, LocalTime time,
                                                     int capacity) {
        AvailableDate availableDate = new AvailableDate(date, time, capacity, restaurant);
        return availableDateRepository.save(availableDate);
    }
}
