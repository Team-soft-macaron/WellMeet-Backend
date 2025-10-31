package com.wellmeet.domain.restaurant.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record RestaurantIdsRequest(
        @NotEmpty
        List<String> restaurantIds
) {
}
