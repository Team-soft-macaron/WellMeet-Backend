package com.wellmeet.domain.member.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wellmeet.domain.fixture.NullAndEmptyAndBlankSource;
import com.wellmeet.domain.member.exception.MemberErrorCode;
import com.wellmeet.domain.member.exception.MemberException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

class MemberTest {

    @Nested
    class ValidateName {

        @ParameterizedTest
        @NullAndEmptyAndBlankSource
        void 멤버_이름은_개행_문자_외_글자가_포함되어야한다(String name) {
            assertThatThrownBy(() -> new Member(name, "nickname", "email", "phone"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NAME_INVALID.getMessage());
        }

        @Test
        void 멤버_이름은_일정_길이_이내여야_한다() {
            String name = "r".repeat(Member.MAX_NAME_LENGTH + 1);

            assertThatThrownBy(() -> new Member(name, "nickname", "email", "phone"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NAME_INVALID.getMessage());
        }
    }

    @Nested
    class ValidateNickname {

        @ParameterizedTest
        @NullAndEmptyAndBlankSource
        void 멤버_닉네임은_개행_문자_외_글자가_포함되어야한다(String nickname) {
            assertThatThrownBy(() -> new Member("name", nickname, "email", "phone"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NICKNAME_INVALID.getMessage());
        }

        @Test
        void 멤버_닉네임은_일정_길이_이내여야_한다() {
            String nickname = "n".repeat(Member.MAX_NICKNAME_LENGTH + 1);

            assertThatThrownBy(() -> new Member("name", nickname, "email", "phone"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NICKNAME_INVALID.getMessage());
        }
    }
}
