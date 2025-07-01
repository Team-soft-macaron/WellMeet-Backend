package com.wellmeet.restaurant.controller;

import static com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName.CLASSIC;
import static com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName.CLEAN;
import static com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName.LIVELY;
import static com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName.MODERN;
import static com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName.values;
import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.domain.crawlingreview.domain.Vibe;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.RecommendRestaurantRequest;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import io.restassured.http.ContentType;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestaurantControllerTest extends BaseControllerTest {

    @Test
    void getRecommendRestaurants() {
        Arrays.stream(values())
                .forEach(vibeName -> vibeRepository.save(new Vibe(vibeName.name())));

        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", 132.1, 123.1);
        Restaurant savedRestaurant1 = restaurantRepository.save(restaurant1);
        createCrawlingReviews(savedRestaurant1, CLASSIC, CLASSIC, CLASSIC, CLEAN, LIVELY);

        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", 132.1, 123.1);
        Restaurant savedRestaurant2 = restaurantRepository.save(restaurant2);
        createCrawlingReviews(savedRestaurant2, CLASSIC, CLASSIC, LIVELY, MODERN);

        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", 132.1, 123.1);
        Restaurant savedRestaurant3 = restaurantRepository.save(restaurant3);
        createCrawlingReviews(savedRestaurant3, CLASSIC, CLASSIC, LIVELY);

        Restaurant restaurant4 = new Restaurant("restaurant4", "address4", 132.1, 123.1);
        Restaurant savedRestaurant4 = restaurantRepository.save(restaurant4);
        createCrawlingReviews(savedRestaurant4, LIVELY, LIVELY, LIVELY);

        RecommendRestaurantRequest request = new RecommendRestaurantRequest(CLASSIC);

        RecommendRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().get("/api/restaurant/recommend")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RecommendRestaurantResponse[].class);

        assertThat(responses).hasSize(3);
        assertThat(responses[0].getId()).isEqualTo(savedRestaurant3.getId());
        assertThat(responses[1].getId()).isEqualTo(savedRestaurant1.getId());
        assertThat(responses[2].getId()).isEqualTo(savedRestaurant2.getId());
    }

    private void createCrawlingReviews(Restaurant restaurant, VibeName... vibeNames) {
        for (VibeName vibeName : vibeNames) {
            crawlingReviewGenerator.generate(restaurant, vibeName);
        }
    }
}
