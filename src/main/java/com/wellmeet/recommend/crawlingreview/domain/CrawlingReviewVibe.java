package com.wellmeet.recommend.crawlingreview.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrawlingReviewVibe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crawling_review_id")
    private CrawlingReview crawlingReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vibe_id")
    private Vibe vibe;

    public CrawlingReviewVibe(CrawlingReview crawlingReview, Vibe vibe) {
        this.crawlingReview = crawlingReview;
        this.vibe = vibe;
    }
}
