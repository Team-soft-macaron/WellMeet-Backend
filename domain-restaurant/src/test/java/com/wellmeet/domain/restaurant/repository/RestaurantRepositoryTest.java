package com.wellmeet.domain.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;

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

    @Nested
    class FindWithBoundBox {

        @Test
        void BoundingBox_내의_식당만_조회한다() {
            String ownerId = "test-owner-id";
            createAndSaveRestaurant("식당1", 37.5, 127.0, ownerId);
            createAndSaveRestaurant("식당2", 37.501, 127.001, ownerId);
            createAndSaveRestaurant("식당3", 38.0, 128.0, ownerId);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        void BoundingBox_밖의_식당은_조회되지_않는다() {
            String ownerId = "test-owner-id";
            createAndSaveRestaurant("먼_식당", 38.0, 128.0, ownerId);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).isEmpty();
        }

        @Test
        void 중심_좌표로부터_일정_반경_내의_식당을_조회한다() {
            String ownerId = "test-owner-id";
            createAndSaveRestaurant("가까운_식당", 37.5, 127.0, ownerId);
            createAndSaveRestaurant("먼_식당", 38.0, 128.0, ownerId);

            BoundingBox boundingBox = new BoundingBox(37.5, 127.0);

            List<Restaurant> result = restaurantRepository.findWithBoundBox(boundingBox);

            assertThat(result).isNotEmpty();
        }
    }

    private Restaurant createAndSaveRestaurant(String name, double lat, double lon, String ownerId) {
        Restaurant restaurant = new Restaurant(
                UUID.randomUUID().toString(),
                name,
                "address",
                lat,
                lon,
                "thumbnail",
                ownerId
        );
        return restaurantRepository.save(restaurant);
    }
}
