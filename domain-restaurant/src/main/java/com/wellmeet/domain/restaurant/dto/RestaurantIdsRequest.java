package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RestaurantIdsRequest(
        @NotEmpty(message = "Restaurant IDs는 비어있을 수 없습니다")
        List<String> restaurantIds
) {
}
