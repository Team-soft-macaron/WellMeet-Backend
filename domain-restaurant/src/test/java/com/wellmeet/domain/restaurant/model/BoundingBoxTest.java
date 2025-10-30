package com.wellmeet.domain.restaurant.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BoundingBoxTest {

    @Nested
    class ValidatePosition {

        @ParameterizedTest
        @ValueSource(doubles = {BoundingBox.MINIMUM_LATITUDE - 0.1, BoundingBox.MAXIMUM_LATITUDE + 0.1})
        void 위도는_일정_범위_이내여야_한다(double latitude) {
            assertThatThrownBy(() -> new BoundingBox(latitude, 37.1))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LATITUDE.getMessage());
        }

        @ParameterizedTest
        @ValueSource(doubles = {BoundingBox.MINIMUM_LONGITUDE - 0.1, BoundingBox.MAXIMUM_LONGITUDE + 0.1})
        void 경도는_일정_범위_이내여야_한다(double longitude) {
            assertThatThrownBy(() -> new BoundingBox(37.1, longitude))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_LONGITUDE.getMessage());
        }
    }
}
