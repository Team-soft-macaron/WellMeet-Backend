package com.wellmeet.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MemberIdsRequest(
        @NotEmpty
        List<String> memberIds
) {
}
