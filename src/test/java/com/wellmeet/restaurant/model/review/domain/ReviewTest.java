package com.wellmeet.restaurant.model.review.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.member.domain.Member;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewTest {

    @DisplayName("rating은 일정 범위 이내여야 한다")
    @ValueSource(doubles = {Review.MINIMUM_RATING - 0.1, Review.MAXIMUM_RATING + 0.1})
    @ParameterizedTest
    void ratingWithRange(double rating) {
        Restaurant restaurant = new Restaurant(UUID.randomUUID(), "Test Restaurant", "Test Address", 37.5665, 126.978,
                "https://example.com/image.jpg");
        Member member = new Member("testuser");

        assertThatThrownBy(() -> new Review("Great food!", rating, Situation.BUSINESS, restaurant, member))
                .isInstanceOf(WellMeetException.class)
                .hasMessageContaining(ErrorCode.INVALID_RATING.getMessage());
    }
}
