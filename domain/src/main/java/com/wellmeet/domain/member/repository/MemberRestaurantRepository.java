package com.wellmeet.domain.member.repository;

import com.wellmeet.domain.member.domain.Member;
import com.wellmeet.domain.member.domain.MemberRestaurant;
import com.wellmeet.domain.restaurant.domain.Restaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRestaurantRepository extends JpaRepository<MemberRestaurant, Long> {

    List<MemberRestaurant> findByMemberId(Long memberId);

    Optional<MemberRestaurant> findByMemberAndRestaurant(Member member, Restaurant restaurant);

    boolean existsByMemberIdAndRestaurantId(Long memberId, String restaurantId);
}
