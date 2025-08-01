package com.wellmeet.review.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedReviewResponse {

    private List<ReviewListResponse> reviews;
    private long total;
    private int page;
    private int totalPages;
}
