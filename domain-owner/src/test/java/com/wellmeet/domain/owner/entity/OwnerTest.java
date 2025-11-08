package com.wellmeet.domain.owner.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.fixture.NullAndEmptyAndBlankSource;
import com.wellmeet.domain.exception.OwnerErrorCode;
import com.wellmeet.domain.exception.OwnerException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

class OwnerTest {

    @Nested
    class ValidateName {

        @ParameterizedTest
        @NullAndEmptyAndBlankSource
        void 사장님_이름은_개행_문자_외_글자가_포함되어야한다(String name) {
            assertThatThrownBy(() -> new Owner(name, "email"))
                    .isInstanceOf(OwnerException.class)
                    .hasMessage(OwnerErrorCode.OWNER_NAME_INVALID.getMessage());
        }

        @Test
        void 사장님_이름은_일정_길이_이내여야한다() {
            String name = "r".repeat(Owner.MAX_NAME_LENGTH + 1);

            assertThatThrownBy(() -> new Owner(name, "email"))
                    .isInstanceOf(OwnerException.class)
                    .hasMessage(OwnerErrorCode.OWNER_NAME_INVALID.getMessage());
        }
    }
}
