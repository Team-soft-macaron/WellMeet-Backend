package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.client.dto.ReviewDTO;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MenuDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class UserRestaurantBffControllerTest extends BaseControllerTest {

    private static final double LATITUDE = 38.5;
    private static final double LONGITUDE = 128.2;

    @MockitoBean
    private UserRestaurantBffService restaurantService;

    @Nested
    class GetNearbyRestaurants {

        @Test
        void 주변_레스토랑_조회() {
            RestaurantDTO restaurant1 = new RestaurantDTO(
                    "restaurant-1",
                    "식당1",
                    "서울시",
                    LATITUDE,
                    LONGITUDE,
                    "thumbnail1.jpg",
                    "owner-1",
                    null,
                    null
            );
            RestaurantDTO restaurant2 = new RestaurantDTO(
                    "restaurant-2",
                    "식당2",
                    "서울시",
                    LATITUDE,
                    LONGITUDE,
                    "thumbnail2.jpg",
                    "owner-1",
                    null,
                    null
            );

            NearbyRestaurantResponse response1 = new NearbyRestaurantResponse(restaurant1, 0.5, 4.5);
            NearbyRestaurantResponse response2 = new NearbyRestaurantResponse(restaurant2, 0.8, 4.0);

            when(restaurantService.findWithNearbyRestaurant(LATITUDE, LONGITUDE))
                    .thenReturn(List.of(response1, response2));

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
            String restaurantId = "restaurant-1";
            String memberId = "member-1";

            RestaurantDTO restaurant = new RestaurantDTO(
                    restaurantId,
                    "테스트 식당",
                    "서울시 강남구",
                    37.5,
                    127.0,
                    "thumbnail.jpg",
                    "owner-1",
                    null,
                    null
            );

            MenuDTO menu1 = new MenuDTO(1L, "메뉴1", "설명1", 10000, restaurantId, null, null);
            MenuDTO menu2 = new MenuDTO(2L, "메뉴2", "설명2", 15000, restaurantId, null, null);
            List<RepresentativeMenuResponse> menus = List.of(
                    new RepresentativeMenuResponse(menu1),
                    new RepresentativeMenuResponse(menu2)
            );

            ReviewDTO review1 = new ReviewDTO(1L, "맛있어요", 5.0, "DATE", restaurantId, memberId);
            ReviewDTO review2 = new ReviewDTO(2L, "좋아요", 4.0, "FAMILY", restaurantId, memberId);
            List<RepresentativeReviewResponse> reviews = List.of(
                    new RepresentativeReviewResponse(review1),
                    new RepresentativeReviewResponse(review2)
            );

            RestaurantResponse restaurantResponse = new RestaurantResponse(
                    restaurant,
                    reviews,
                    menus,
                    true,
                    4.5
            );

            when(restaurantService.getRestaurant(restaurantId, memberId))
                    .thenReturn(restaurantResponse);

            RestaurantResponse response = given()
                    .contentType(ContentType.JSON)
                    .queryParam("memberId", memberId)
                    .when().get("/user/restaurant/{id}", restaurantId)
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(RestaurantResponse.class);

            assertThat(response.getId()).isEqualTo(restaurantId);
            assertThat(response.getMenus()).hasSize(2);
            assertThat(response.getReviews()).hasSize(2);
        }
    }

    @Nested
    class GetRestaurantAvailableDates {

        @Test
        void 예약_가능_시간_조회() {
            String restaurantId = "restaurant-1";

            AvailableDateDTO availableDate1 = new AvailableDateDTO(
                    1L,
                    LocalDate.now().plusDays(1),
                    LocalTime.of(18, 0),
                    10,
                    true,
                    restaurantId,
                    null,
                    null
            );
            AvailableDateDTO availableDate2 = new AvailableDateDTO(
                    2L,
                    LocalDate.now().plusDays(2),
                    LocalTime.of(19, 0),
                    20,
                    true,
                    restaurantId,
                    null,
                    null
            );

            List<AvailableDateResponse> availableDateResponses = List.of(
                    new AvailableDateResponse(availableDate1),
                    new AvailableDateResponse(availableDate2)
            );

            when(restaurantService.getRestaurantAvailableDates(restaurantId))
                    .thenReturn(availableDateResponses);

            AvailableDateResponse[] responses = given()
                    .contentType(ContentType.JSON)
                    .when().get("/user/restaurant/{id}/available", restaurantId)
                    .then().statusCode(HttpStatus.OK.value())
                    .extract().as(AvailableDateResponse[].class);

            assertThat(responses).hasSize(2);
        }
    }
}