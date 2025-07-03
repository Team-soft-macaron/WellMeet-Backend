package com.wellmeet.favorite.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.favorite.dto.FavoriteRestaurantRequest;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import com.wellmeet.member.domain.Member;
import com.wellmeet.member.domain.MemberRestaurant;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FavoriteControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("즐겨찾기 레스토랑 조회")
    void getFavoriteRestaurants() {
        Member testUser = memberRepository.save(new Member("testUser"));
        Member anotherUser = memberRepository.save(new Member("anotherUser"));
        Restaurant restaurant1 = restaurantRepository.save(
                new Restaurant("Restaurant 1", "Address 1", 38.5, 128.2, "https://example.com/restaurant1.jpg"));
        Restaurant restaurant2 = restaurantRepository.save(
                new Restaurant("Restaurant 2", "Address 2", 38.5, 128.2, "https://example.com/restaurant2.jpg"));
        Restaurant restaurant3 = restaurantRepository.save(
                new Restaurant("Restaurant 3", "Address 3", 38.5, 128.2, "https://example.com/restaurant3.jpg"));
        memberRestaurantRepository.save(new MemberRestaurant(testUser, restaurant1));
        memberRestaurantRepository.save(new MemberRestaurant(testUser, restaurant2));
        memberRestaurantRepository.save(new MemberRestaurant(anotherUser, restaurant2));
        memberRestaurantRepository.save(new MemberRestaurant(anotherUser, restaurant3));

        FavoriteRestaurantResponse[] responses = given()
                .contentType("application/json")
                .queryParam("memberId", testUser.getId())
                .when().get("/api/favorite/restaurants")
                .then().statusCode(HttpStatus.OK.value())
                .extract().as(FavoriteRestaurantResponse[].class);

        assertThat(responses).hasSize(2);
        assertThat(responses[0].getId()).isEqualTo(restaurant1.getId());
        assertThat(responses[1].getId()).isEqualTo(restaurant2.getId());
    }

    @Test
    @DisplayName("즐겨찾기 레스토랑 추가")
    void addFavoriteRestaurant() {
        Member testUser = memberRepository.save(new Member("testUser"));
        Restaurant restaurant = restaurantRepository.save(
                new Restaurant("Restaurant 1", "Address 1", 38.5, 128.2, "https://example.com/restaurant1.jpg"));
        FavoriteRestaurantRequest request = new FavoriteRestaurantRequest(restaurant.getId());

        FavoriteRestaurantResponse response = given()
                .contentType("application/json")
                .queryParam("memberId", testUser.getId())
                .body(request)
                .when().post("/api/favorite/restaurant")
                .then().statusCode(HttpStatus.CREATED.value())
                .extract().as(FavoriteRestaurantResponse.class);

        assertThat(response.getId()).isEqualTo(restaurant.getId());
    }
}
