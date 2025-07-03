package com.wellmeet.recommend.crawlingreview.repository;

import com.wellmeet.recommend.crawlingreview.domain.CrawlingReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CrawlingReviewRepository extends JpaRepository<CrawlingReview, Long> {
}
