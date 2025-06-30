package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RecommendRestaurantRequest {

    private final VibeName vibeName;
}
