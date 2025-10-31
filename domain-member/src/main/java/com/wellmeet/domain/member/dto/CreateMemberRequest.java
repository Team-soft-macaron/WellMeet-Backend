package com.wellmeet.domain.member.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateMemberRequest(
        @NotBlank
        String name,

        @NotBlank
        String nickname,

        @NotBlank
        String email,

        @NotBlank
        String phone
) {
}
