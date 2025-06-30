package com.wellmeet.restaurant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseRepositoryTest;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.repository.crawlingreview.repository.CrawlingReviewRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RestaurantRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CrawlingReviewRepository crawlingReviewRepository;

    @Test
    void findRestaurantsOrderedByVibeRatio() {
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", 132.1, 123.1);
        Restaurant savedRestaurant1 = restaurantRepository.save(restaurant1);
        crawlingReviewGenerator.generate(savedRestaurant1, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant1, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant1, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant1, VibeName.CLEAN);
        crawlingReviewGenerator.generate(savedRestaurant1, VibeName.LIVELY);

        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", 132.1, 123.1);
        Restaurant savedRestaurant2 = restaurantRepository.save(restaurant2);
        crawlingReviewGenerator.generate(savedRestaurant2, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant2, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant2, VibeName.LIVELY);
        crawlingReviewGenerator.generate(savedRestaurant2, VibeName.MODERN);

        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", 132.1, 123.1);
        Restaurant savedRestaurant3 = restaurantRepository.save(restaurant3);
        crawlingReviewGenerator.generate(savedRestaurant3, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant3, VibeName.CLASSIC);
        crawlingReviewGenerator.generate(savedRestaurant3, VibeName.LIVELY);

        Restaurant restaurant4 = new Restaurant("restaurant4", "address4", 132.1, 123.1);
        Restaurant savedRestaurant4 = restaurantRepository.save(restaurant4);
        crawlingReviewGenerator.generate(savedRestaurant4, VibeName.LIVELY);
        crawlingReviewGenerator.generate(savedRestaurant4, VibeName.LIVELY);
        crawlingReviewGenerator.generate(savedRestaurant4, VibeName.LIVELY);

        List<Restaurant> restaurantsOrderedByVibeRatio = restaurantRepository.findRestaurantsOrderedByVibeRatio(
                VibeName.CLASSIC.name());

        assertThat(restaurantsOrderedByVibeRatio).hasSize(3);
        assertThat(restaurantsOrderedByVibeRatio.getFirst().getId()).isEqualTo(savedRestaurant3.getId());
        assertThat(restaurantsOrderedByVibeRatio.get(1).getId()).isEqualTo(savedRestaurant1.getId());
        assertThat(restaurantsOrderedByVibeRatio.get(2).getId()).isEqualTo(savedRestaurant2.getId());
    }
}
