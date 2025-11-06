package com.wellmeet.common.dto.request;

import java.util.List;

public record MemberIdsRequest(
        List<String> memberIds
) {
}
