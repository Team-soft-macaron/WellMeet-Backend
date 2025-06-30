package com.wellmeet.restaurant.tool;

import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.domain.crawlingreview.domain.CrawlingReview;
import com.wellmeet.restaurant.domain.crawlingreview.domain.CrawlingReviewVibe;
import com.wellmeet.restaurant.domain.crawlingreview.domain.Vibe;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.repository.crawlingreview.repository.CrawlingReviewRepository;
import com.wellmeet.restaurant.repository.crawlingreview.repository.CrawlingReviewVibeRepository;
import com.wellmeet.restaurant.repository.crawlingreview.repository.VibeRepository;
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
        CrawlingReview crawlingReview = new CrawlingReview("content", 4.5, restaurant);
        CrawlingReview savedCrawlingReview = crawlingReviewRepository.save(crawlingReview);
        Vibe vibe = vibeRepository.findByName(vibeName.name())
                .orElseThrow();
        crawlingReviewVibeRepository.save(new CrawlingReviewVibe(savedCrawlingReview, vibe));
    }
}
