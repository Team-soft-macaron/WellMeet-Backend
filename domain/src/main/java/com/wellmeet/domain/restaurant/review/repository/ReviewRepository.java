package com.wellmeet.domain.restaurant.review.repository;

import com.wellmeet.domain.restaurant.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantId(String restaurantId);
}
