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
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import io.restassured.http.ContentType;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestaurantControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;
    private static final String MAIN_IMAGE = "https://example.com/restaurant.jpg";

    @BeforeEach
    void setEnvironment() {
        Arrays.stream(values())
                .forEach(vibeName -> vibeRepository.save(new Vibe(vibeName.name())));
    }

    @Test
    @DisplayName("추천 레스토랑 조회 - vibe에 따른 비율로 정렬")
    void getRecommendRestaurants() {
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

        RecommendRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .when().get("/api/restaurants/recommend?" +
                        "vibe=" + CLASSIC.name() +
                        "&latitude=" + LATITUDE +
                        "&longitude=" + LONGITUDE)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RecommendRestaurantResponse[].class);

        assertThat(responses).hasSize(3);
        assertThat(responses[0].getId()).isEqualTo(savedRestaurant3.getId());
        assertThat(responses[1].getId()).isEqualTo(savedRestaurant1.getId());
        assertThat(responses[2].getId()).isEqualTo(savedRestaurant2.getId());
    }

    @Test
    @DisplayName("주변 레스토랑 조회")
    void getNearbyRestaurants() {
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, MAIN_IMAGE);
        restaurantRepository.save(restaurant1);
        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", LATITUDE, LONGITUDE, MAIN_IMAGE);
        restaurantRepository.save(restaurant2);
        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", LATITUDE - 3, LONGITUDE + 3, MAIN_IMAGE);
        restaurantRepository.save(restaurant3);

        RecommendRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .when().get("/api/restaurants/nearby?latitude=" + LATITUDE + "&longitude=" + LONGITUDE)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RecommendRestaurantResponse[].class);

        assertThat(responses).hasSize(2);
    }

    private void createCrawlingReviews(Restaurant restaurant, VibeName... vibeNames) {
        for (VibeName vibeName : vibeNames) {
            crawlingReviewGenerator.generate(restaurant, vibeName);
        }
    }
}
