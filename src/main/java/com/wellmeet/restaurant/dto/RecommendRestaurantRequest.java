package com.wellmeet.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class RecommendRestaurantRequest {

    @JsonProperty("vibe")
    private VibeName vibeName;
}
