package com.wellmeet.favorite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.client.FavoriteRestaurantClient;
import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.FavoriteRestaurantDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRestaurantClient favoriteRestaurantClient;

    @Mock
    private RestaurantClient restaurantClient;

    @InjectMocks
    private FavoriteService favoriteService;

    @Nested
    class GetFavoriteRestaurants {

        @Test
        void 즐겨찾기_식당_목록을_조회한다() {
            String memberId = "member-1";
            FavoriteRestaurantDTO favorite1 = createFavoriteRestaurantDTO(memberId, "restaurant-1");
            FavoriteRestaurantDTO favorite2 = createFavoriteRestaurantDTO(memberId, "restaurant-2");
            List<FavoriteRestaurantDTO> favorites = List.of(favorite1, favorite2);

            RestaurantDTO restaurant1 = createRestaurantDTO("restaurant-1", "식당1");
            RestaurantDTO restaurant2 = createRestaurantDTO("restaurant-2", "식당2");

            when(favoriteRestaurantClient.getFavoritesByMemberId(memberId))
                    .thenReturn(favorites);
            when(restaurantClient.getAverageRating("restaurant-1")).thenReturn(4.5);
            when(restaurantClient.getAverageRating("restaurant-2")).thenReturn(3.8);
            when(restaurantClient.getRestaurantsByIds(any()))
                    .thenReturn(List.of(restaurant1, restaurant2));

            List<FavoriteRestaurantResponse> result = favoriteService.getFavoriteRestaurants(memberId);

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(FavoriteRestaurantResponse::getName, FavoriteRestaurantResponse::getRating)
                    .containsExactlyInAnyOrder(
                            tuple("식당1", 4.5),
                            tuple("식당2", 3.8)
                    );
        }

        @Test
        void 즐겨찾기가_없으면_빈_리스트를_반환한다() {
            String memberId = "member-1";

            when(favoriteRestaurantClient.getFavoritesByMemberId(memberId))
                    .thenReturn(List.of());

            List<FavoriteRestaurantResponse> result = favoriteService.getFavoriteRestaurants(memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class AddFavoriteRestaurant {

        @Test
        void 즐겨찾기_식당을_추가한다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";
            RestaurantDTO restaurant = createRestaurantDTO(restaurantId, "맛집");

            when(restaurantClient.getRestaurant(restaurantId)).thenReturn(restaurant);
            when(restaurantClient.getAverageRating(restaurantId)).thenReturn(4.2);

            FavoriteRestaurantResponse result = favoriteService.addFavoriteRestaurant(
                    memberId,
                    restaurantId
            );

            assertThat(result.getId()).isEqualTo(restaurantId);
            assertThat(result.getName()).isEqualTo("맛집");
            assertThat(result.getRating()).isEqualTo(4.2);
            verify(favoriteRestaurantClient).addFavorite(memberId, restaurantId);
        }
    }

    @Nested
    class RemoveFavoriteRestaurant {

        @Test
        void 즐겨찾기_식당을_삭제한다() {
            String memberId = "member-1";
            String restaurantId = "restaurant-1";

            favoriteService.removeFavoriteRestaurant(memberId, restaurantId);

            verify(favoriteRestaurantClient).removeFavorite(memberId, restaurantId);
        }
    }

    private FavoriteRestaurantDTO createFavoriteRestaurantDTO(String memberId, String restaurantId) {
        return new FavoriteRestaurantDTO(1L, memberId, restaurantId);
    }

    private RestaurantDTO createRestaurantDTO(String id, String name) {
        return RestaurantDTO.builder()
                .id(id)
                .name(name)
                .address("서울시 강남구")
                .latitude(37.5)
                .longitude(127.0)
                .thumbnail("thumbnail.jpg")
                .ownerId("owner-1")
                .build();
    }
}
