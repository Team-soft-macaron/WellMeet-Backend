package com.wellmeet.domain.member.entity;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.domain.favorite.entity.FavoriteRestaurant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FavoriteRestaurantTest {

    @Nested
    class Constructor {

        @Test
        void 즐겨찾기_레스토랑을_생성한다() {
            String memberId = "member-id";
            String restaurantId = "restaurant-id";

            FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(memberId, restaurantId);

            assertThat(favoriteRestaurant.getMemberId()).isEqualTo(memberId);
            assertThat(favoriteRestaurant.getRestaurantId()).isEqualTo(restaurantId);
        }
    }
}
