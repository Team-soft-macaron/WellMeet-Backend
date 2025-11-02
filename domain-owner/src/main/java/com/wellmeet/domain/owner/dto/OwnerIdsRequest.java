package com.wellmeet.domain.owner.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OwnerIdsRequest(
        @NotEmpty(message = "조회할 사업자 ID 목록은 필수입니다")
        List<String> ownerIds
) {
}
