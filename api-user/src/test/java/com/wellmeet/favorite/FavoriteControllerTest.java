package com.wellmeet.favorite;

import static org.assertj.core.api.Assertions.assertThat;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class FavoriteControllerTest extends BaseControllerTest {

    @Nested
    class GetFavoriteRestaurants {

        @Test
        void 즐겨찾기_레스토랑_조회() {
            Member testUser = memberGenerator.generate("test");
            Member anotherUser = memberGenerator.generate("another");
            Owner owner1 = ownerGenerator.generate("Owner1");
            Owner owner2 = ownerGenerator.generate("Owner2");
            Owner owner3 = ownerGenerator.generate("Owner3");
            Restaurant restaurant1 = restaurantGenerator.generate("Restaurant 1", owner1);
            Restaurant restaurant2 = restaurantGenerator.generate("Restaurant 2", owner2);
            Restaurant restaurant3 = restaurantGenerator.generate("Restaurant 3", owner3);
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser.getId(), restaurant1.getId()));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser.getId(), restaurant2.getId()));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(anotherUser.getId(), restaurant2.getId()));
            favoriteRestaurantRepository.save(new FavoriteRestaurant(anotherUser.getId(), restaurant3.getId()));

            FavoriteRestaurantResponse[] responses = given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().get("/user/favorite/restaurant/list")
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(FavoriteRestaurantResponse[].class);

            assertThat(responses).hasSize(2);
            assertThat(responses[0].getId()).isEqualTo(restaurant1.getId());
            assertThat(responses[1].getId()).isEqualTo(restaurant2.getId());
        }
    }

    @Nested
    class AddFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_추가() {
            Member testUser = memberGenerator.generate("testUser");
            Owner owner = ownerGenerator.generate("Test Owner");
            Restaurant restaurant = restaurantGenerator.generate("Test Restaurant", owner);

            FavoriteRestaurantResponse response = given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().post("/user/favorite/restaurant/{restaurantId}", restaurant.getId())
                    .then().statusCode(HttpStatus.CREATED.value())
                    .extract().as(FavoriteRestaurantResponse.class);

            assertThat(response.getId()).isEqualTo(restaurant.getId());
        }
    }

    @Nested
    class RemoveFavoriteRestaurant {

        @Test
        void 즐겨찾기_레스토랑_삭제() {
            Member testUser = memberGenerator.generate("testUser");
            Owner owner = ownerGenerator.generate("Test Owner");
            Restaurant restaurant = restaurantGenerator.generate("Test Restaurant", owner);
            favoriteRestaurantRepository.save(new FavoriteRestaurant(testUser.getId(), restaurant.getId()));

            given()
                    .contentType("application/json")
                    .queryParam("memberId", testUser.getId())
                    .when().delete("/user/favorite/restaurant/{restaurantId}", restaurant.getId())
                    .then().statusCode(HttpStatus.NO_CONTENT.value());
        }
    }
}
