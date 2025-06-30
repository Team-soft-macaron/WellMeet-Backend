package com.wellmeet.restaurant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendRestaurantRequest {

    @JsonProperty("vibe")
    private VibeName vibeName;
}
