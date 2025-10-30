package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import io.restassured.http.ContentType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class RestaurantControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;

    @Nested
    class GetNearbyRestaurants {

        @Test
        void 주변_레스토랑_조회() {
            Owner owner = ownerGenerator.generate("owner1");
            restaurantGenerator.generate("restaurant1", LATITUDE, LONGITUDE, owner.getId());
            restaurantGenerator.generate("restaurant2", LATITUDE, LONGITUDE, owner.getId());
            restaurantGenerator.generate("restaurant3", LATITUDE + 5, LONGITUDE - 5, owner.getId());

            NearbyRestaurantResponse[] responses = given()
                    .contentType(ContentType.JSON)
                    .when().get("/user/restaurant/nearby?latitude=" + LATITUDE + "&longitude=" + LONGITUDE)
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(NearbyRestaurantResponse[].class);

            assertThat(responses).hasSize(2);
        }
    }

    @Nested
    class GetRestaurant {

        @Test
        void 레스토랑_상세_조회() {
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner.getId());
            menuGenerator.generate("menu1", 10000, restaurant);
            menuGenerator.generate("menu2", 15000, restaurant);
            Member member = memberGenerator.generate("testMember");
            reviewGenerator.generate(5, restaurant, member.getId());
            reviewGenerator.generate(4, restaurant, member.getId());

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

    @Nested
    class GetRestaurantAvailableDates {

        @Test
        void 예약_가능_시간_조회() {
            Owner owner = ownerGenerator.generate("owner1");
            Restaurant restaurant = restaurantGenerator.generate("restaurant1", owner.getId());
            availableDateGenerator.generate(LocalDateTime.now().plusDays(1), 10, restaurant);
            availableDateGenerator.generate(LocalDateTime.now().plusDays(2), 20, restaurant);

            AvailableDateResponse[] responses = given()
                    .contentType(ContentType.JSON)
                    .when().get("/user/restaurant/{id}/available", restaurant.getId())
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(AvailableDateResponse[].class);

            assertThat(responses).hasSize(2);
        }
    }
}
