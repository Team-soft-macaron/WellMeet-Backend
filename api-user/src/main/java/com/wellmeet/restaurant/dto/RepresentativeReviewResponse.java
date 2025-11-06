package com.wellmeet.restaurant.dto;

import com.wellmeet.client.dto.ReviewDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RepresentativeReviewResponse {

    private String situation;
    private String content;

    public RepresentativeReviewResponse(ReviewDTO review) {
        this.situation = review.situation();
        this.content = review.content();
    }
}
