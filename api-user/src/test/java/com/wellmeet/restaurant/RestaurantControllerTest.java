package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestaurantControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;

    @Test
    @DisplayName("주변 레스토랑 조회")
    void getNearbyRestaurants() {
        Owner owner = ownerGenerator.generate("owner1");
        restaurantGenerator.generate("restaurant1", LATITUDE, LONGITUDE, owner);
        restaurantGenerator.generate("restaurant2", LATITUDE, LONGITUDE, owner);
        restaurantGenerator.generate("restaurant3", LATITUDE + 5, LONGITUDE - 5, owner);

        NearbyRestaurantResponse[] responses = given()
                .contentType(ContentType.JSON)
                .when().get("/user/restaurant/nearby?latitude=" + LATITUDE + "&longitude=" + LONGITUDE)
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(NearbyRestaurantResponse[].class);

        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("레스토랑 상세 조회")
    void getRestaurant() {
        Owner owner = ownerGenerator.generate("owner1");
        Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner);
        menuGenerator.generate("menu1", 10000, restaurant);
        menuGenerator.generate("menu2", 15000, restaurant);
        Member member = memberGenerator.generate("testMember");
        reviewGenerator.generate(5, restaurant, member);
        reviewGenerator.generate(4, restaurant, member);

        RestaurantResponse restaurantResponse = given()
                .contentType(ContentType.JSON)
                .queryParam("memberId", member.getId())
                .when().get("/user/restaurant/{id}", restaurant.getId())
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(RestaurantResponse.class);

        assertThat(restaurantResponse.getId()).isEqualTo(restaurant.getId());
        assertThat(restaurantResponse.getMenus()).hasSize(2);
        assertThat(restaurantResponse.getReviews()).hasSize(2);
    }
}
