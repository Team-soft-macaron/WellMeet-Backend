package com.wellmeet.restaurant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.entity.Situation;
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
class DomainRestaurantServiceTest {

    @Mock
    private RestaurantDomainService restaurantDomainService;

    @Mock
    private FavoriteRestaurantDomainService favoriteRestaurantDomainService;

    @InjectMocks
    private RestaurantService restaurantService;

    @Nested
    class FindWithNearbyRestaurant {

        @Test
        void 주변_식당을_조회한다() {
            double latitude = 37.5;
            double longitude = 127.0;
            Restaurant restaurant1 = createRestaurant("restaurant-1", "식당1", 37.501, 127.001);
            Restaurant restaurant2 = createRestaurant("restaurant-2", "식당2", 37.502, 127.002);
            List<Restaurant> restaurants = List.of(restaurant1, restaurant2);

            when(restaurantDomainService.findWithBoundBox(latitude, longitude))
                    .thenReturn(restaurants);
            when(restaurantDomainService.getAverageRating("restaurant-1"))
                    .thenReturn(4.5);
            when(restaurantDomainService.getAverageRating("restaurant-2"))
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

            when(restaurantDomainService.findWithBoundBox(latitude, longitude))
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
            Restaurant restaurant = createRestaurant(restaurantId, "식당1", 37.5, 127.0);
            Review review = createReview(restaurant);
            Menu menu = createMenu(restaurant);

            when(favoriteRestaurantDomainService.isFavorite(memberId, restaurantId))
                    .thenReturn(true);
            when(restaurantDomainService.getById(restaurantId))
                    .thenReturn(restaurant);
            when(restaurantDomainService.getReviewByRestaurantId(restaurantId))
                    .thenReturn(List.of(review));
            when(restaurantDomainService.getMenuByRestaurantId(restaurantId))
                    .thenReturn(List.of(menu));
            when(restaurantDomainService.getAverageRating(restaurantId))
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
            Restaurant restaurant = createRestaurant(restaurantId, "식당1", 37.5, 127.0);

            when(favoriteRestaurantDomainService.isFavorite(memberId, restaurantId))
                    .thenReturn(false);
            when(restaurantDomainService.getById(restaurantId))
                    .thenReturn(restaurant);
            when(restaurantDomainService.getReviewByRestaurantId(restaurantId))
                    .thenReturn(List.of());
            when(restaurantDomainService.getMenuByRestaurantId(restaurantId))
                    .thenReturn(List.of());
            when(restaurantDomainService.getAverageRating(restaurantId))
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
            Restaurant restaurant = createRestaurant(restaurantId, "식당1", 37.5, 127.0);
            AvailableDate availableDate1 = createAvailableDate(LocalDate.now().plusDays(1), LocalTime.of(18, 0), 10,
                    restaurant);
            AvailableDate availableDate2 = createAvailableDate(LocalDate.now().plusDays(2), LocalTime.of(19, 0), 5,
                    restaurant);

            when(restaurantDomainService.getRestaurantAvailableDates(restaurantId))
                    .thenReturn(List.of(availableDate1, availableDate2));

            List<AvailableDateResponse> responses = restaurantService.getRestaurantAvailableDates(restaurantId);

            assertThat(responses).hasSize(2);
        }

        @Test
        void 예약_가능한_날짜가_없으면_빈_리스트를_반환한다() {
            String restaurantId = "restaurant-1";

            when(restaurantDomainService.getRestaurantAvailableDates(restaurantId))
                    .thenReturn(List.of());

            List<AvailableDateResponse> responses = restaurantService.getRestaurantAvailableDates(restaurantId);

            assertThat(responses).isEmpty();
        }
    }

    private Restaurant createRestaurant(String id, String name, double lat, double lon) {
        Owner owner = new Owner("owner-name", "owner@email.com");
        return new Restaurant(id, name, "서울시", lat, lon, "thumbnail.jpg", owner.getId());
    }

    private Review createReview(Restaurant restaurant) {
        Member member = new Member("member", "nickname", "email@test.com", "010-1234-5678");
        return new Review("맛있어요", 4.5, Situation.DATE, restaurant, member.getId());
    }

    private Menu createMenu(Restaurant restaurant) {
        return new Menu("메뉴1", "맛있는 메뉴", 10000, restaurant);
    }

    private AvailableDate createAvailableDate(LocalDate date, LocalTime time, int capacity, Restaurant restaurant) {
        return new AvailableDate(date, time, capacity, restaurant);
    }
}
