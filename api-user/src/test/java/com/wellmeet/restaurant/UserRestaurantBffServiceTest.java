package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.MemberFavoriteRestaurantFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MenuDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.ReviewDTO;
import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserRestaurantBffServiceTest {

    @Mock
    private RestaurantFeignClient restaurantClient;

    @Mock
    private MemberFavoriteRestaurantFeignClient favoriteRestaurantClient;

    @Mock
    private RestaurantAvailableDateFeignClient availableDateClient;

    @InjectMocks
    private UserRestaurantBffService restaurantService;

    @Nested
    class FindWithNearbyRestaurant {

        @Test
        void 주변_식당을_조회한다() {
            double latitude = 37.5;
            double longitude = 127.0;
            RestaurantDTO restaurant1 = RestaurantDTO.builder()
                    .id("restaurant-1")
                    .name("식당1")
                    .address("서울시")
                    .latitude(37.501)
                    .longitude(127.001)
                    .thumbnail("thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();
            RestaurantDTO restaurant2 = RestaurantDTO.builder()
                    .id("restaurant-2")
                    .name("식당2")
                    .address("서울시")
                    .latitude(37.502)
                    .longitude(127.002)
                    .thumbnail("thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();
            List<RestaurantDTO> restaurants = List.of(restaurant1, restaurant2);

            when(restaurantClient.getAllRestaurants())
                    .thenReturn(restaurants);
            when(restaurantClient.getAverageRating("restaurant-1"))
                    .thenReturn(4.5);
            when(restaurantClient.getAverageRating("restaurant-2"))
                    .thenReturn(4.0);

            List<NearbyRestaurantResponse> responses = restaurantService.findWithNearbyRestaurant(latitude, longitude);

            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting(NearbyRestaurantResponse::getName, NearbyRestaurantResponse::getRating)
                    .containsExactlyInAnyOrder(
                            tuple("식당1", 4.5),
                            tuple("식당2", 4.0)
                    );
        }

        @Test
        void 주변에_식당이_없으면_빈_리스트를_반환한다() {
            double latitude = 37.5;
            double longitude = 127.0;

            when(restaurantClient.getAllRestaurants())
                    .thenReturn(List.of());

            List<NearbyRestaurantResponse> responses = restaurantService.findWithNearbyRestaurant(latitude, longitude);

            assertThat(responses).isEmpty();
        }
    }

    @Nested
    class GetRestaurant {

        @Test
        void 식당_상세_정보를_조회한다() {
            String restaurantId = "restaurant-1";
            String memberId = "member-1";
            RestaurantDTO restaurant = RestaurantDTO.builder()
                    .id(restaurantId)
                    .name("식당1")
                    .address("서울시")
                    .latitude(37.5)
                    .longitude(127.0)
                    .thumbnail("thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();
            ReviewDTO review = new ReviewDTO(1L, "맛있어요", 4.5, "DATE", restaurantId, memberId);
            MenuDTO menu = new MenuDTO(1L, "메뉴1", "맛있는 메뉴", 10000, restaurantId);

            when(favoriteRestaurantClient.isFavorite(memberId, restaurantId))
                    .thenReturn(true);
            when(restaurantClient.getRestaurant(restaurantId))
                    .thenReturn(restaurant);
            when(restaurantClient.getReviewsByRestaurant(restaurantId))
                    .thenReturn(List.of(review));
            when(restaurantClient.getMenusByRestaurant(restaurantId))
                    .thenReturn(List.of(menu));
            when(restaurantClient.getAverageRating(restaurantId))
                    .thenReturn(4.5);

            RestaurantResponse response = restaurantService.getRestaurant(restaurantId, memberId);

            assertThat(response.getName()).isEqualTo("식당1");
            assertThat(response.isFavorite()).isTrue();
            assertThat(response.getRating()).isEqualTo(4.5);
            assertThat(response.getReviews()).hasSize(1);
            assertThat(response.getMenus()).hasSize(1);
        }

        @Test
        void 즐겨찾기하지_않은_식당을_조회한다() {
            String restaurantId = "restaurant-1";
            String memberId = "member-1";
            RestaurantDTO restaurant = RestaurantDTO.builder()
                    .id(restaurantId)
                    .name("식당1")
                    .address("서울시")
                    .latitude(37.5)
                    .longitude(127.0)
                    .thumbnail("thumbnail.jpg")
                    .ownerId("owner-1")
                    .build();

            when(favoriteRestaurantClient.isFavorite(memberId, restaurantId))
                    .thenReturn(false);
            when(restaurantClient.getRestaurant(restaurantId))
                    .thenReturn(restaurant);
            when(restaurantClient.getReviewsByRestaurant(restaurantId))
                    .thenReturn(List.of());
            when(restaurantClient.getMenusByRestaurant(restaurantId))
                    .thenReturn(List.of());
            when(restaurantClient.getAverageRating(restaurantId))
                    .thenReturn(0.0);

            RestaurantResponse response = restaurantService.getRestaurant(restaurantId, memberId);

            assertThat(response.isFavorite()).isFalse();
        }
    }

    @Nested
    class GetRestaurantAvailableDates {

        @Test
        void 식당의_예약_가능한_날짜를_조회한다() {
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
                    .maxCapacity(5)
                    .isAvailable(true)
                    .restaurantId(restaurantId)
                    .build();

            when(availableDateClient.getAvailableDatesByRestaurant(restaurantId))
                    .thenReturn(List.of(availableDate1, availableDate2));

            List<AvailableDateResponse> responses = restaurantService.getRestaurantAvailableDates(restaurantId);

            assertThat(responses).hasSize(2);
        }

        @Test
        void 예약_가능한_날짜가_없으면_빈_리스트를_반환한다() {
            String restaurantId = "restaurant-1";

            when(availableDateClient.getAvailableDatesByRestaurant(restaurantId))
                    .thenReturn(List.of());

            List<AvailableDateResponse> responses = restaurantService.getRestaurantAvailableDates(restaurantId);

            assertThat(responses).isEmpty();
        }
    }
}