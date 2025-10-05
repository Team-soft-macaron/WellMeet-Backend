package com.wellmeet.domain.restaurant.availabledate.repository;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AvailableDateRepository extends JpaRepository<AvailableDate, Long> {

    List<AvailableDate> findAllByRestaurantId(String restaurantId);

    Optional<AvailableDate> findByIdAndRestaurantId(Long id, String restaurantId);

    @Modifying(clearAutomatically = true)
    @Query("update AvailableDate a "
            + "set a.isAvailable = case when (a.maxCapacity - :partySize) = 0 then false else a.isAvailable end, "
            + "a.maxCapacity = a.maxCapacity - :partySize "
            + "where a.id = :id and a.maxCapacity >= :partySize")
    int decreaseCapacity(@Param("id") Long id, @Param("partySize") int partySize);

    @Modifying(clearAutomatically = true)
    @Query("update AvailableDate a set a.maxCapacity = a.maxCapacity + :partySize, a.isAvailable = true where a.id = :id")
    void increaseCapacity(@Param("id") Long id, @Param("partySize") int partySize);
}
