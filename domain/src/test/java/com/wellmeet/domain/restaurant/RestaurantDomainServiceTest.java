package com.wellmeet.domain.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.availabledate.AvailableDateDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.availabledate.repository.AvailableDateRepository;
import com.wellmeet.domain.restaurant.businesshour.BusinessHourDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import com.wellmeet.domain.restaurant.menu.MenuDomainService;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import com.wellmeet.domain.restaurant.review.ReviewDomainService;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({
        RestaurantDomainService.class,
        ReviewDomainService.class,
        AvailableDateDomainService.class,
        MenuDomainService.class,
        BusinessHourDomainService.class
})
class RestaurantDomainServiceTest extends BaseRepositoryTest {

    @Autowired
    private RestaurantDomainService restaurantDomainService;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private AvailableDateRepository availableDateRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private EntityManager entityManager;

    @Nested
    class GetById {

        @Test
        void 식당을_조회한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant("맛집", owner);

            Restaurant result = restaurantDomainService.getById(restaurant.getId());

            assertThat(result.getId()).isEqualTo(restaurant.getId());
            assertThat(result.getName()).isEqualTo("맛집");
        }

        @Test
        void 존재하지_않는_식당_조회_시_예외가_발생한다() {
            String nonExistentId = "non-existent-id";

            assertThatThrownBy(() -> restaurantDomainService.getById(nonExistentId))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessageContaining(RestaurantErrorCode.RESTAURANT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    class FindWithBoundBox {

        @Test
        void BoundingBox를_계산하여_주변_식당을_조회한다() {
            Owner owner = createAndSaveOwner();
            createAndSaveRestaurant("식당1", 37.5, 127.0, owner);
            createAndSaveRestaurant("식당2", 37.501, 127.001, owner);
            createAndSaveRestaurant("먼식당", 38.0, 128.0, owner);

            double userLat = 37.5;
            double userLon = 127.0;

            List<Restaurant> result = restaurantDomainService.findWithBoundBox(userLat, userLon);

            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void 반경_내에_식당이_없으면_빈_리스트를_반환한다() {
            Owner owner = createAndSaveOwner();
            createAndSaveRestaurant("먼식당", 38.0, 128.0, owner);

            double userLat = 37.5;
            double userLon = 127.0;

            List<Restaurant> result = restaurantDomainService.findWithBoundBox(userLat, userLon);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class DecreaseAvailableDateCapacity {

        @Test
        void 예약_가능_날짜의_수용_인원을_감소시킨다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant("식당", owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 10);

            restaurantDomainService.decreaseAvailableDateCapacity(availableDate, 4);
            entityManager.flush();
            entityManager.clear();

            AvailableDate result = availableDateRepository.findById(availableDate.getId()).orElseThrow();
            assertThat(result.getMaxCapacity()).isEqualTo(6);
        }

        @Test
        void 수용_인원이_부족하면_예외가_발생한다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant("식당", owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 2);

            assertThatThrownBy(() -> restaurantDomainService.decreaseAvailableDateCapacity(availableDate, 4))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessageContaining(RestaurantErrorCode.NOT_ENOUGH_CAPACITY.getMessage());
        }
    }

    @Nested
    class IncreaseAvailableDateCapacity {

        @Test
        void 예약_가능_날짜의_수용_인원을_증가시킨다() {
            Owner owner = createAndSaveOwner();
            Restaurant restaurant = createAndSaveRestaurant("식당", owner);
            AvailableDate availableDate = createAndSaveAvailableDate(restaurant, 5);

            restaurantDomainService.increaseAvailableDateCapacity(availableDate, 3);
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

    private Restaurant createAndSaveRestaurant(String name, Owner owner) {
        return createAndSaveRestaurant(name, 37.5, 127.0, owner);
    }

    private Restaurant createAndSaveRestaurant(String name, double lat, double lon, Owner owner) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID().toString(),
                name,
                "address",
                lat,
                lon,
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
