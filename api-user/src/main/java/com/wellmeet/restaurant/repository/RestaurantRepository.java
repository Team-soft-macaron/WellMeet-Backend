package com.wellmeet.restaurant.repository;

import com.wellmeet.restaurant.domain.BoundingBox;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

    @Query(value = """
            SELECT r
            FROM Restaurant r
            WHERE r.latitude BETWEEN :#{#boundingBox.minLatitude} AND :#{#boundingBox.maxLatitude}
            AND r.longitude BETWEEN :#{#boundingBox.minLongitude} AND :#{#boundingBox.maxLongitude}
            """)
    List<Restaurant> findWithBoundBox(@Param("boundingBox") BoundingBox boundingBox);
}
