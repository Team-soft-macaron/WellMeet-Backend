package com.wellmeet.common.dto;

import java.time.LocalDateTime;

public record MemberDTO(
        String id,
        String name,
        String nickname,
        String email,
        String phone,
        boolean reservationEnabled,
        boolean remindEnabled,
        boolean reviewEnabled,
        boolean isVip,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
