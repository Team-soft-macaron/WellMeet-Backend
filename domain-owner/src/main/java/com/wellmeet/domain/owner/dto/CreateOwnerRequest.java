package com.wellmeet.domain.owner.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOwnerRequest(
        @NotBlank(message = "이름은 필수입니다")
        @Size(max = 10, message = "이름은 최대 10자입니다")
        String name,

        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "유효한 이메일 형식이 아닙니다")
        String email
) {
}
