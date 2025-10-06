package com.wellmeet.domain.member.entity;

import static org.assertj.core.api.Assertions.*;

import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FavoriteRestaurantTest {

    @Nested
    class Constructor {

        @Test
        void 즐겨찾기_레스토랑을_생성한다() {
            Member member = createMember();
            Restaurant restaurant = createRestaurant();

            FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(member, restaurant);

            assertThat(favoriteRestaurant.getMember()).isEqualTo(member);
            assertThat(favoriteRestaurant.getRestaurant()).isEqualTo(restaurant);
        }
    }

    private Member createMember() {
        return new Member("name", "nickname", "email@example.com", "010-1234-5678");
    }

    private Restaurant createRestaurant() {
        Owner owner = new Owner("owner", "owner@example.com");
        return new Restaurant("restaurant", "description", "address", 37.5, 127.0, "thumbnail", owner);
    }
}
