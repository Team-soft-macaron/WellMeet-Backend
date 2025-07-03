package com.wellmeet.recommend.crawlingreview.repository;

import com.wellmeet.recommend.crawlingreview.domain.CrawlingReviewVibe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingReviewVibeRepository extends JpaRepository<CrawlingReviewVibe, Long> {
}
