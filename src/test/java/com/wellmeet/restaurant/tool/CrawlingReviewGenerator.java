package com.wellmeet.restaurant.tool;

import com.wellmeet.recommend.crawlingreview.domain.CrawlingReview;
import com.wellmeet.recommend.crawlingreview.domain.CrawlingReviewVibe;
import com.wellmeet.recommend.crawlingreview.domain.Vibe;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.recommend.crawlingreview.repository.CrawlingReviewVibeRepository;
import com.wellmeet.recommend.crawlingreview.repository.VibeRepository;
import com.wellmeet.restaurant.domain.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class CrawlingReviewGenerator {

    private final CrawlingReviewRepository crawlingReviewRepository;
    private final CrawlingReviewVibeRepository crawlingReviewVibeRepository;
    private final VibeRepository vibeRepository;

    public CrawlingReviewGenerator(
            CrawlingReviewRepository crawlingReviewRepository,
            CrawlingReviewVibeRepository crawlingReviewVibeRepository,
            VibeRepository vibeRepository
    ) {
        this.crawlingReviewRepository = crawlingReviewRepository;
        this.crawlingReviewVibeRepository = crawlingReviewVibeRepository;
        this.vibeRepository = vibeRepository;
    }

    public void generate(Restaurant restaurant, VibeName vibeName) {
        CrawlingReview crawlingReview = new CrawlingReview("content", restaurant);
        CrawlingReview savedCrawlingReview = crawlingReviewRepository.save(crawlingReview);
        Vibe vibe = vibeRepository.findByName(vibeName).orElseThrow();
        crawlingReviewVibeRepository.save(new CrawlingReviewVibe(savedCrawlingReview, vibe));
    }
}
