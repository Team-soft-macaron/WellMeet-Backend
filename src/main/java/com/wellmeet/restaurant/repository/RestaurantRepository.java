package com.wellmeet.restaurant.repository;

import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = """
            SELECT r
            FROM Restaurant r
            WHERE EXISTS (
                SELECT 1 FROM CrawlingReview cr WHERE cr.restaurant = r
            )
            AND EXISTS (
                        SELECT 1\s
                        FROM CrawlingReview cr3
                        JOIN CrawlingReviewVibe crv3 ON crv3.crawlingReview = cr3
                        JOIN Vibe v3 ON crv3.vibe = v3
                        WHERE cr3.restaurant = r AND v3.name = :vibeName
                    )
            ORDER BY (
                SELECT CAST(COUNT(CASE WHEN v.name = :vibeName THEN 1 END) AS double) / NULLIF(COUNT(crv), 0)
                FROM CrawlingReview cr2
                JOIN CrawlingReviewVibe crv ON crv.crawlingReview = cr2
                JOIN Vibe v ON crv.vibe = v
                WHERE cr2.restaurant = r
            ) DESC
            """)
    List<Restaurant> findRestaurantsOrderedByVibeRatio(@Param("vibeName") String vibeName);
}
