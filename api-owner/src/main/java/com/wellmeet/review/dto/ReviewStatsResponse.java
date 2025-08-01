package com.wellmeet.review.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsResponse {

    private double avgRating;           // 평균 별점
    private long totalCount;            // 전체 리뷰 수
    private Map<String, Long> distribution; // 별점별 분포 ("1": count, "2": count, ...)
    private double replyRate;           // 답글 작성률 (%)
    private String recentTrend;         // 최근 추세 ("up", "down", "stable")
}
