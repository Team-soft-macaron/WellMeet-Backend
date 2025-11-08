package com.wellmeet.domain.restaurant.review.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.fixture.NullAndEmptyAndBlankSource;
import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import com.wellmeet.domain.review.entity.ReviewTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

class ReviewTagTest {

    @Nested
    class ValidateName {

        @ParameterizedTest
        @NullAndEmptyAndBlankSource
        void 태그_이름은_개행_문자_외_글자가_포함되어야한다(String name) {
            assertThatThrownBy(() -> new ReviewTag(null, name))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_REVIEW_TAG_NAME.getMessage());
        }

        @Test
        void 태그_이름은_일정_길이_이내여야한다() {
            String name = "t".repeat(ReviewTag.MAX_NAME_LENGTH + 1);

            assertThatThrownBy(() -> new ReviewTag(null, name))
                    .isInstanceOf(RestaurantException.class)
                    .hasMessage(RestaurantErrorCode.INVALID_REVIEW_TAG_NAME.getMessage());
        }
    }
}
