package com.wellmeet.restaurant.model.review.repository;

import com.wellmeet.restaurant.model.review.domain.Review;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByRestaurantId(UUID restaurantId);
}
