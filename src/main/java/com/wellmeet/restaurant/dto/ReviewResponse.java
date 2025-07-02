package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.domain.review.domain.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewResponse {

    private String situation;
    private String content;
    private String logo;

    public ReviewResponse(Review review) {
        this.situation = review.getSituation().getName();
        this.content = review.getContent();
        this.logo = review.getSituation().getLogo();
    }
}
