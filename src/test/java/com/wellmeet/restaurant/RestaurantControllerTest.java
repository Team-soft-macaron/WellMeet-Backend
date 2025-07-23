package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.member.domain.Member;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import com.wellmeet.restaurant.model.menu.domain.Menu;
import com.wellmeet.restaurant.model.review.domain.Review;
import com.wellmeet.restaurant.model.review.domain.Situation;
import io.restassured.http.ContentType;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestaurantControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;
    private static final String THUMBNAIL = "https://example.com/restaurant.jpg";

    @Test
    @DisplayName("주변 레스토랑 조회")
    void getNearbyRestaurants() {
        Restaurant restaurant1 = new Restaurant(UUID.randomUUID().toString(), "restaurant1", "address1", LATITUDE,
                LONGITUDE,
                THUMBNAIL);
        restaurantRepository.save(restaurant1);
        Restaurant restaurant2 = new Restaurant(UUID.randomUUID().toString(), "restaurant2", "address2", LATITUDE,
                LONGITUDE,
                THUMBNAIL);
        restaurantRepository.save(restaurant2);
        Restaurant restaurant3 = new Restaurant(UUID.randomUUID().toString(), "restaurant3", "address3", LATITUDE - 3,
                LONGITUDE + 3, THUMBNAIL);
        restaurantRepository.save(restaurant3);

        NearbyRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .when().get("/api/restaurant/nearby?latitude=" + LATITUDE + "&longitude=" + LONGITUDE)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(NearbyRestaurantResponse[].class);

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("레스토랑 상세 조회")
    void getRestaurant() {
        Restaurant restaurant = new Restaurant(UUID.randomUUID().toString(), "restaurant1", "address1", LATITUDE,
                LONGITUDE,
                THUMBNAIL);
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
                .queryParam("memberId", member.getId())
                .when().get("/api/restaurant/{id}", restaurant.getId())
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RestaurantResponse.class);

        assertThat(restaurantResponse.getId()).isEqualTo(restaurant.getId());
        assertThat(restaurantResponse.getMenus()).hasSize(2);
        assertThat(restaurantResponse.getReviews()).hasSize(2);
    }
}
