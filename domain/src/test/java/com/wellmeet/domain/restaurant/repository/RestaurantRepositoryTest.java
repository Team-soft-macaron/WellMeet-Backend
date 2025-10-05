package com.wellmeet.domain.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.owner.repository.OwnerRepository;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.model.BoundingBox;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestaurantRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Nested
    class FindWithBoundBox {

        @Test
        void BoundingBox_내의_식당만_조회한다() {
            Owner owner = createAndSaveOwner();
            createAndSaveRestaurant("식당1", 37.5, 127.0, owner);
            createAndSaveRestaurant("식당2", 37.501, 127.001, owner);
            createAndSaveRestaurant("식당3", 38.0, 128.0, owner);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void BoundingBox_밖의_식당은_조회되지_않는다() {
            Owner owner = createAndSaveOwner();
            createAndSaveRestaurant("먼_식당", 38.0, 128.0, owner);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).isEmpty();
        }

        @Test
        void 중심_좌표로부터_일정_반경_내의_식당을_조회한다() {
            Owner owner = createAndSaveOwner();
            createAndSaveRestaurant("가까운_식당", 37.5, 127.0, owner);
            createAndSaveRestaurant("먼_식당", 38.0, 128.0, owner);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).isNotEmpty();
        }
    }

    private Owner createAndSaveOwner() {
        Owner owner = new Owner("owner", "owner@test.com");
        return ownerRepository.save(owner);
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
}
