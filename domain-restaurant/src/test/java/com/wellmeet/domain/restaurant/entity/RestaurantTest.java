package com.wellmeet.domain.restaurant.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RestaurantTest {

    @Nested
    class ValidatePosition {

        @ParameterizedTest
        @ValueSource(doubles = {Restaurant.MINIMUM_LATITUDE - 0.1, Restaurant.MAXIMUM_LATITUDE + 0.1})
        void 위도는_일정_범위_이내여야_한다(double latitude) {
            assertThatThrownBy(() -> new Restaurant(
                    UUID.randomUUID().toString(),
                    "name",
                    "address",
                    latitude,
                    37.1,
                    "thumbnail",
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LATITUDE.getMessage());
        }

        @ParameterizedTest
        @ValueSource(doubles = {Restaurant.MINIMUM_LONGITUDE - 0.1, Restaurant.MAXIMUM_LONGITUDE + 0.1})
        void 경도는_일정_범위_이내여야_한다(double longitude) {
            assertThatThrownBy(() -> new Restaurant(
                    UUID.randomUUID().toString(),
                    "name",
                    "address",
                    37.1,
                    longitude,
                    "thumbnail",
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LONGITUDE.getMessage());
        }
    }
}
