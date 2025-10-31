package com.wellmeet.domain.member.dto;

import com.wellmeet.domain.member.entity.Member;
import java.time.LocalDateTime;

public record MemberResponse(
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
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getEmail(),
                member.getPhone(),
                member.isReservationEnabled(),
                member.isRemindEnabled(),
                member.isReviewEnabled(),
                member.isVip(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}