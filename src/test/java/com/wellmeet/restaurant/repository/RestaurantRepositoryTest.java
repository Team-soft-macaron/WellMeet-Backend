package com.wellmeet.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.restaurant.domain.BoundingBox;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestaurantRepositoryTest extends BaseRepositoryTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;
    private static final String THUMBNAIL = "https://example.com/restaurant.jpg";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    @DisplayName("주변 레스토랑 조회 - BoundingBox를 이용한 레스토랑 조회")
    void findWithBoundBox() {
        Restaurant restaurant1 = new Restaurant(UUID.randomUUID(), "restaurant1", "address1", LATITUDE, LONGITUDE,
                THUMBNAIL);
        restaurantRepository.save(restaurant1);
        Restaurant restaurant2 = new Restaurant(UUID.randomUUID(), "restaurant2", "address2", LATITUDE, LONGITUDE,
                THUMBNAIL);
        restaurantRepository.save(restaurant2);
        Restaurant restaurant3 = new Restaurant(UUID.randomUUID(), "restaurant3", "address3", LATITUDE - 3,
                LONGITUDE + 3, THUMBNAIL);
        restaurantRepository.save(restaurant3);

        BoundingBox boundingBox = new BoundingBox(LATITUDE, LONGITUDE);
        List<Restaurant> restaurants = restaurantRepository.findWithBoundBox(boundingBox);

        assertThat(restaurants).hasSize(2);
    }
}
