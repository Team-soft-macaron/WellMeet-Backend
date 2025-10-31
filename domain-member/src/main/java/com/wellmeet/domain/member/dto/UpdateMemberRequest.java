package com.wellmeet.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateMemberRequest(
        @Size(max = 10, message = "이름은 최대 10자까지 가능합니다")
        String name,

        @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다")
        String nickname,

        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        String phone,

        Boolean reservationEnabled,

        Boolean remindEnabled,

        Boolean reviewEnabled
) {
}
