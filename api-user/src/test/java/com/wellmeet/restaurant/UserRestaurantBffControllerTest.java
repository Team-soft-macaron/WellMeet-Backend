package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.wellmeet.BaseControllerTest;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MenuDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.ReviewDTO;
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
            RestaurantDTO restaurant1 = RestaurantDTO.builder()
                    .id("restaurant-1")
                    .name("식당1")
                    .address("서울시")
                    .latitude(LATITUDE)
                    .longitude(LONGITUDE)
                    .thumbnail("thumbnail1.jpg")
                    .ownerId("owner-1")
                    .build();
            RestaurantDTO restaurant2 = RestaurantDTO.builder()
                    .id("restaurant-2")
                    .name("식당2")
                    .address("서울시")
                    .latitude(LATITUDE)
                    .longitude(LONGITUDE)
                    .thumbnail("thumbnail2.jpg")
                    .ownerId("owner-1")
                    .build();

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

            RestaurantDTO restaurant = RestaurantDTO.builder()
                    .id(restaurantId)
                    .name("테스트 식당")
                    .address("서울시 강남구")
                    .latitude(37.5)
                    .longitude(127.0)
                    .thumbnail("thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();

            MenuDTO menu1 = new MenuDTO(1L, "메뉴1", "설명1", 10000, restaurantId);
            MenuDTO menu2 = new MenuDTO(2L, "메뉴2", "설명2", 15000, restaurantId);
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

            AvailableDateDTO availableDate1 = AvailableDateDTO.builder()
                    .id(1L)
                    .date(LocalDate.now().plusDays(1))
                    .time(LocalTime.of(18, 0))
                    .maxCapacity(10)
                    .isAvailable(true)
                    .restaurantId(restaurantId)
                    .build();
            AvailableDateDTO availableDate2 = AvailableDateDTO.builder()
                    .id(2L)
                    .date(LocalDate.now().plusDays(2))
                    .time(LocalTime.of(19, 0))
                    .maxCapacity(20)
                    .isAvailable(true)
                    .restaurantId(restaurantId)
                    .build();

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