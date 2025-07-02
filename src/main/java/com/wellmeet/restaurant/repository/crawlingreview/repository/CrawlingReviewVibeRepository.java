package com.wellmeet.restaurant.repository.crawlingreview.repository;

import com.wellmeet.restaurant.domain.crawlingreview.domain.CrawlingReviewVibe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingReviewVibeRepository extends JpaRepository<CrawlingReviewVibe, Long> {
}
