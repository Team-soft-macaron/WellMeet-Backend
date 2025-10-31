package com.wellmeet.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MemberIdsRequest(
        @NotEmpty(message = "Member IDs는 비어있을 수 없습니다")
        List<String> memberIds
) {
}
