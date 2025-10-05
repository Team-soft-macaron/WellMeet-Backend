package com.wellmeet.domain.restaurant.availabledate.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AvailableDateRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    class DecreaseCapacity {

        @Test
        void 예약_인원만큼_수용_인원을_감소시킨다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 10);

            int updated = availableDateRepository.decreaseCapacity(availableDate.getId(), 4);

            assertThat(updated).isEqualTo(1);

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(6);
            assertThat(result.isAvailable()).isTrue();
        }

        @Test
        void 수용_인원이_0이_되면_예약_불가능_상태로_변경된다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 4);

            int updated = availableDateRepository.decreaseCapacity(availableDate.getId(), 4);

            assertThat(updated).isEqualTo(1);

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isZero();
            assertThat(result.isAvailable()).isFalse();
        }

        @Test
        void 수용_인원이_부족하면_업데이트하지_않는다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 2);

            int updated = availableDateRepository.decreaseCapacity(availableDate.getId(), 4);

            assertThat(updated).isZero();

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(2);
        }
    }

    @Nested
    class IncreaseCapacity {

        @Test
        void 수용_인원을_증가시키고_예약_가능_상태로_변경한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 2);

            availableDateRepository.increaseCapacity(availableDate.getId(), 4);

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(6);
            assertThat(result.isAvailable()).isTrue();
        }

        @Test
        void 여러_번_호출하면_누적으로_증가한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant(owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 10);

            availableDateRepository.increaseCapacity(availableDate.getId(), 2);
            availableDateRepository.increaseCapacity(availableDate.getId(), 3);

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(15);
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

    private AvailableDate createAndSaveAvailableDate(Restaurant restaurant, int capacity) {
        AvailableDate availableDate = new AvailableDate(
                LocalDate.of(2025, 12, 25),
                LocalTime.of(18, 0),
                capacity,
                restaurant
        );
        return availableDateRepository.save(availableDate);
    }
}
