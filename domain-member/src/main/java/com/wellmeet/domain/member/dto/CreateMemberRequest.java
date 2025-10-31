package com.wellmeet.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateMemberRequest(
        @NotBlank(message = "이름은 비어있을 수 없습니다")
        @Size(max = 10, message = "이름은 최대 10자까지 가능합니다")
        String name,

        @NotBlank(message = "닉네임은 비어있을 수 없습니다")
        @Size(max = 10, message = "닉네임은 최대 10자까지 가능합니다")
        String nickname,

        @NotBlank(message = "이메일은 비어있을 수 없습니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @NotBlank(message = "전화번호는 비어있을 수 없습니다")
        String phone
) {
}