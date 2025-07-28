package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.review.domain.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativeReviewResponse {

    private String situation;
    private String content;
    private String logo;

    public RepresentativeReviewResponse(Review review) {
        this.situation = review.getSituation().getName();
        this.content = review.getContent();
        this.logo = review.getSituation().getLogo();
    }
}
