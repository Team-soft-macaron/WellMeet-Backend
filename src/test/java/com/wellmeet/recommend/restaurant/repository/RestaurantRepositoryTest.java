package com.wellmeet.recommend.restaurant.repository;

import static com.wellmeet.recommend.crawlingreview.domain.VibeName.CLASSIC;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.CLEAN;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.LIVELY;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.MODERN;
import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.recommend.crawlingreview.domain.Vibe;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.crawlingreview.repository.VibeRepository;
import com.wellmeet.recommend.restaurant.domain.BoundingBox;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestaurantRepositoryTest extends BaseRepositoryTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;
    private static final String MAIN_IMAGE = "https://example.com/restaurant.jpg";

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    protected VibeRepository vibeRepository;

    @BeforeEach
    void setEnvironment() {
        Arrays.stream(VibeName.values())
                .forEach(vibeName -> vibeRepository.save(new Vibe(vibeName)));
    }

    @Test
    @DisplayName("추천 레스토랑 조회 - vibe에 따른 비율로 정렬")
    void findRestaurantsOrderedByVibeRatioWithBoundBox() {
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, MAIN_IMAGE);
        Restaurant savedRestaurant1 = restaurantRepository.save(restaurant1);
        createCrawlingReviews(savedRestaurant1, CLASSIC, CLASSIC, CLASSIC, CLEAN, LIVELY);

        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", LATITUDE, LONGITUDE, MAIN_IMAGE);
        Restaurant savedRestaurant2 = restaurantRepository.save(restaurant2);
        createCrawlingReviews(savedRestaurant2, CLASSIC, CLASSIC, LIVELY, MODERN);

        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", LATITUDE, LONGITUDE, MAIN_IMAGE);
        Restaurant savedRestaurant3 = restaurantRepository.save(restaurant3);
        createCrawlingReviews(savedRestaurant3, CLASSIC, CLASSIC, LIVELY);

        Restaurant restaurant4 = new Restaurant("restaurant4", "address4", LATITUDE, LONGITUDE, MAIN_IMAGE);
        Restaurant savedRestaurant4 = restaurantRepository.save(restaurant4);
        createCrawlingReviews(savedRestaurant4, LIVELY, LIVELY, LIVELY);

        BoundingBox boundingBox = new BoundingBox(LATITUDE, LONGITUDE);
        List<Restaurant> restaurantsOrderedByVibeRatio = restaurantRepository.findRestaurantsOrderedByVibeRatioWithBoundBox(
                CLASSIC, boundingBox
        );

        assertThat(restaurantsOrderedByVibeRatio).hasSize(3);
        assertThat(restaurantsOrderedByVibeRatio.getFirst().getId()).isEqualTo(savedRestaurant3.getId());
        assertThat(restaurantsOrderedByVibeRatio.get(1).getId()).isEqualTo(savedRestaurant1.getId());
        assertThat(restaurantsOrderedByVibeRatio.get(2).getId()).isEqualTo(savedRestaurant2.getId());
    }

    @Test
    @DisplayName("주변 레스토랑 조회 - BoundingBox를 이용한 레스토랑 조회")
    void findWithBoundBox() {
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, MAIN_IMAGE);
        restaurantRepository.save(restaurant1);
        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", LATITUDE, LONGITUDE, MAIN_IMAGE);
        restaurantRepository.save(restaurant2);
        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", LATITUDE - 3, LONGITUDE + 3, MAIN_IMAGE);
        restaurantRepository.save(restaurant3);

        BoundingBox boundingBox = new BoundingBox(LATITUDE, LONGITUDE);
        List<Restaurant> restaurants = restaurantRepository.findWithBoundBox(boundingBox);

        assertThat(restaurants).hasSize(2);
    }

    private void createCrawlingReviews(Restaurant restaurant, VibeName... vibeNames) {
        for (VibeName vibeName : vibeNames) {
            crawlingReviewGenerator.generate(restaurant, vibeName);
        }
    }
}
