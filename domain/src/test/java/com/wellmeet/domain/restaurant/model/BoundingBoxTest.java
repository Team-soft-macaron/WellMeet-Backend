package com.wellmeet.domain.restaurant.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BoundingBoxTest {

    @DisplayName("latitude는 일정 범위 이내여야 한다")
    @ValueSource(doubles = {BoundingBox.MINIMUM_LATITUDE - 0.1, BoundingBox.MAXIMUM_LATITUDE + 0.1})
    @ParameterizedTest
    void latitudeWithRange(double latitude) {
        assertThatThrownBy(() -> new BoundingBox(latitude, 0.0))
                .isInstanceOf(WellMeetDomainException.class)
                .hasMessageContaining(DomainErrorCode.INVALID_LATITUDE.getMessage());
    }

    @DisplayName("longitude는 일정 범위 이내여야 한다")
    @ValueSource(doubles = {BoundingBox.MINIMUM_LONGITUDE - 0.1, BoundingBox.MAXIMUM_LONGITUDE + 0.1})
    @ParameterizedTest
    void longitudeWithRange(double longitude) {
        assertThatThrownBy(() -> new BoundingBox(0.0, longitude))
                .isInstanceOf(WellMeetDomainException.class)
                .hasMessageContaining(DomainErrorCode.INVALID_LONGITUDE.getMessage());
    }
}
