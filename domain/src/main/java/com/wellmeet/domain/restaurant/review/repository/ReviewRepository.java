package com.wellmeet.domain.restaurant.review.repository;

import com.wellmeet.domain.restaurant.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantId(String restaurantId);

    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.restaurant.id = :restaurantId")
    double getAverageRating(@Param("restaurantId") String restaurantId);
}
