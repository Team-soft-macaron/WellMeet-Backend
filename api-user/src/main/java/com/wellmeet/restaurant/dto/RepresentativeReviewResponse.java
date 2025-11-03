package com.wellmeet.restaurant.dto;

import com.wellmeet.client.dto.ReviewDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativeReviewResponse {

    private String situation;
    private String content;
    private String logo;

    public RepresentativeReviewResponse(ReviewDTO review) {
        this.situation = review.getSituation();
        this.content = review.getContent();
        this.logo = review.getSituationLogo();
    }
}
