package com.wellmeet.domain.restaurant.review.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.fixture.NullAndEmptyAndBlankSource;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewTest {

    @Nested
    class ValidateContent {

        @ParameterizedTest
        @NullAndEmptyAndBlankSource
        void 리뷰_내용은_개행_문자_외_글자가_포함되어야한다(String content) {
            assertThatThrownBy(() -> new Review(
                    content,
                    2.5,
                    Situation.DATE,
                    null,
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_REVIEW_CONTENT.getMessage());
        }

        @Test
        void 리뷰_내용은_일정_길이_이내여야한다() {
            String content = "c".repeat(Review.MAX_CONTENT_LENGTH + 1);

            assertThatThrownBy(() -> new Review(
                    content,
                    2.5,
                    Situation.DATE,
                    null,
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_REVIEW_CONTENT.getMessage());
        }
    }

    @Nested
    class ValidateRating {

        @ParameterizedTest
        @ValueSource(doubles = {Review.MINIMUM_RATING - 0.1, Review.MAXIMUM_RATING + 0.1})
        void 리뷰_평점은_일정_범위_이내여야_한다(double rating) {
            assertThatThrownBy(() -> new Review(
                    "content",
                    rating,
                    Situation.DATE,
                    null,
                    null
            )).isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_RATING.getMessage());
        }
    }
}
