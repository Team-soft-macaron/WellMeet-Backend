package com.wellmeet.recommend;

import static com.wellmeet.recommend.crawlingreview.domain.VibeName.CLASSIC;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.CLEAN;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.LIVELY;
import static com.wellmeet.recommend.crawlingreview.domain.VibeName.MODERN;
import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.member.domain.Member;
import com.wellmeet.recommend.crawlingreview.domain.Vibe;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.menu.domain.Menu;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import com.wellmeet.recommend.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.recommend.restaurant.dto.RestaurantResponse;
import com.wellmeet.recommend.review.domain.Review;
import com.wellmeet.recommend.review.domain.Situation;
import io.restassured.http.ContentType;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RecommendControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;
    private static final String THUMBNAIL = "https://example.com/restaurant.jpg";

    @BeforeEach
    void setEnvironment() {
        Arrays.stream(VibeName.values())
                .forEach(vibeName -> vibeRepository.save(new Vibe(vibeName)));
    }

    @Test
    @DisplayName("추천 레스토랑 조회 - vibe에 따른 비율로 정렬")
    void getRecommendRestaurants() {
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, THUMBNAIL);
        Restaurant savedRestaurant1 = restaurantRepository.save(restaurant1);
        createCrawlingReviews(savedRestaurant1, CLASSIC, CLASSIC, CLASSIC, CLEAN, LIVELY);

        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", LATITUDE, LONGITUDE, THUMBNAIL);
        Restaurant savedRestaurant2 = restaurantRepository.save(restaurant2);
        createCrawlingReviews(savedRestaurant2, CLASSIC, CLASSIC, LIVELY, MODERN);

        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", LATITUDE, LONGITUDE, THUMBNAIL);
        Restaurant savedRestaurant3 = restaurantRepository.save(restaurant3);
        createCrawlingReviews(savedRestaurant3, CLASSIC, CLASSIC, LIVELY);

        Restaurant restaurant4 = new Restaurant("restaurant4", "address4", LATITUDE, LONGITUDE, THUMBNAIL);
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
        Restaurant restaurant1 = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, THUMBNAIL);
        restaurantRepository.save(restaurant1);
        Restaurant restaurant2 = new Restaurant("restaurant2", "address2", LATITUDE, LONGITUDE, THUMBNAIL);
        restaurantRepository.save(restaurant2);
        Restaurant restaurant3 = new Restaurant("restaurant3", "address3", LATITUDE - 3, LONGITUDE + 3, THUMBNAIL);
        restaurantRepository.save(restaurant3);

        RecommendRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .when().get("/api/restaurants/nearby?latitude=" + LATITUDE + "&longitude=" + LONGITUDE)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RecommendRestaurantResponse[].class);

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("레스토랑 상세 조회")
    void getRestaurant() {
        Restaurant restaurant = new Restaurant("restaurant1", "address1", LATITUDE, LONGITUDE, THUMBNAIL);
        restaurantRepository.save(restaurant);
        Menu menu1 = new Menu("menu1", "description1", 10000, restaurant);
        menuRepository.save(menu1);
        Menu menu2 = new Menu("menu2", "description2", 15000, restaurant);
        menuRepository.save(menu2);
        Member member = new Member("nickname");
        memberRepository.save(member);
        Review review1 = new Review("review1", 5, Situation.DATE, restaurant, member);
        reviewRepository.save(review1);
        Review review2 = new Review("review2", 4, Situation.BUSINESS, restaurant, member);
        reviewRepository.save(review2);

        RestaurantResponse restaurantResponse = given()
                .contentType(ContentType.JSON)
                .when().get("/api/restaurant/{id}", restaurant.getId())
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RestaurantResponse.class);

        assertThat(restaurantResponse.getId()).isEqualTo(restaurant.getId());
        assertThat(restaurantResponse.getMenus()).hasSize(2);
        assertThat(restaurantResponse.getReviews()).hasSize(2);
    }

    private void createCrawlingReviews(Restaurant restaurant, VibeName... vibeNames) {
        for (VibeName vibeName : vibeNames) {
            crawlingReviewGenerator.generate(restaurant, vibeName);
        }
    }
}
