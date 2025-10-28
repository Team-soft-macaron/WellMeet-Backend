package com.wellmeet.favorite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.review.ReviewDomainService;
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
    private FavoriteRestaurantDomainService favoriteRestaurantDomainService;

    @Mock
    private ReviewDomainService reviewDomainService;

    @Mock
    private RestaurantDomainService restaurantDomainService;

    @InjectMocks
    private FavoriteService favoriteService;

    @Nested
    class GetFavoriteRestaurants {

        @Test
        void 즐겨찾기_식당_목록을_조회한다() {
            Member member = createMember();
            Restaurant restaurant1 = createRestaurant("restaurant-1", "식당1");
            Restaurant restaurant2 = createRestaurant("restaurant-2", "식당2");
            FavoriteRestaurant favorite1 = new FavoriteRestaurant(member.getId(), restaurant1.getId());
            FavoriteRestaurant favorite2 = new FavoriteRestaurant(member.getId(), restaurant2.getId());
            List<FavoriteRestaurant> favorites = List.of(favorite1, favorite2);

            when(favoriteRestaurantDomainService.findAllByMemberId(member.getId()))
                    .thenReturn(favorites);
            when(reviewDomainService.getAverageRating("restaurant-1")).thenReturn(4.5);
            when(reviewDomainService.getAverageRating("restaurant-2")).thenReturn(3.8);
            when(restaurantDomainService.findAllByIds(List.of(restaurant1.getId(), restaurant2.getId())))
                    .thenReturn(List.of(restaurant1, restaurant2));

            List<FavoriteRestaurantResponse> result = favoriteService.getFavoriteRestaurants(member.getId());

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

            when(favoriteRestaurantDomainService.findAllByMemberId(memberId))
                    .thenReturn(List.of());

            List<FavoriteRestaurantResponse> result = favoriteService.getFavoriteRestaurants(memberId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    class AddFavoriteRestaurant {

        @Test
        void 즐겨찾기_식당을_추가한다() {
            Member member = createMember();
            Restaurant restaurant = createRestaurant("restaurant-1", "맛집");

            when(restaurantDomainService.getById(restaurant.getId())).thenReturn(restaurant);
            when(reviewDomainService.getAverageRating(restaurant.getId())).thenReturn(4.2);

            FavoriteRestaurantResponse result = favoriteService.addFavoriteRestaurant(
                    member.getId(),
                    restaurant.getId()
            );

            assertThat(result.getId()).isEqualTo(restaurant.getId());
            assertThat(result.getName()).isEqualTo("맛집");
            assertThat(result.getRating()).isEqualTo(4.2);
            verify(favoriteRestaurantDomainService).save(
                    any(FavoriteRestaurant.class)
            );
        }
    }

    @Nested
    class RemoveFavoriteRestaurant {

        @Test
        void 즐겨찾기_식당을_삭제한다() {
            Member member = createMember();
            Restaurant restaurant = createRestaurant("restaurant-1", "식당");
            FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(member.getId(), restaurant.getId());

            when(favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(
                    member.getId(),
                    restaurant.getId()
            )).thenReturn(favoriteRestaurant);

            favoriteService.removeFavoriteRestaurant(member.getId(), restaurant.getId());

            verify(favoriteRestaurantDomainService).delete(favoriteRestaurant);
        }
    }

    private Member createMember() {
        return new Member("member", "nickname", "email@test.com", "010-1234-5678");
    }

    private Restaurant createRestaurant(String id, String name) {
        Owner owner = new Owner("owner", "owner@test.com");
        return new Restaurant(
                id,
                name,
                "서울시 강남구",
                37.5,
                127.0,
                "thumbnail.jpg",
                owner
        );
    }
}
