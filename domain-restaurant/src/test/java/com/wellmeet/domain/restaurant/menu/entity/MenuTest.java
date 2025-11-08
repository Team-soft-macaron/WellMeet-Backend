package com.wellmeet.domain.restaurant.menu.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import com.wellmeet.domain.menu.entity.Menu;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MenuTest {

    @Nested
    class ValidatePrice {

        @Test
        void 가격은_음수_일_수_없다() {
            int price = -1;

            assertThatThrownBy(() -> new Menu("name", "description", price, null))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_MENU_PRICE.getMessage());
        }
    }
}
