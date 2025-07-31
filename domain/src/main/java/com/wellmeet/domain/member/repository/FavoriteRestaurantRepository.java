package com.wellmeet.domain.member.repository;

import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRestaurantRepository extends JpaRepository<FavoriteRestaurant, Long> {

    List<FavoriteRestaurant> findByMemberId(Long memberId);

    boolean existsByMemberIdAndRestaurantId(Long memberId, String restaurantId);

    Optional<FavoriteRestaurant> findByMemberIdAndRestaurantId(Long memberId, String restaurantId);
}
