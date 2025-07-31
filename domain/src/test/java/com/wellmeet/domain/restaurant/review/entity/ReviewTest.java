package com.wellmeet.domain.restaurant.review.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewTest {

    @DisplayName("rating은 일정 범위 이내여야 한다")
    @ValueSource(doubles = {Review.MINIMUM_RATING - 0.1, Review.MAXIMUM_RATING + 0.1})
    @ParameterizedTest
    void ratingWithRange(double rating) {
        Restaurant restaurant = new Restaurant(UUID.randomUUID().toString(), "Test Restaurant", "Test Address", 37.5665,
                126.978,
                "https://example.com/image.jpg");
        Member member = new Member("testuser", "test", "email@email.com");

        assertThatThrownBy(() -> new Review("Great food!", rating, Situation.BUSINESS, restaurant, member))
                .isInstanceOf(WellMeetDomainException.class)
                .hasMessageContaining(DomainErrorCode.INVALID_RATING.getMessage());
    }
}
