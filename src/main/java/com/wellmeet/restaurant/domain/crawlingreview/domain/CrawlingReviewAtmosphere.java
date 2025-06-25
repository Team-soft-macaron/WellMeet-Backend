package com.wellmeet.restaurant.domain.crawlingreview.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlingReviewAtmosphere {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawling_review_id")
    private CrawlingReview crawlingReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atmosphere_id")
    private Atmosphere atmosphere;
}
