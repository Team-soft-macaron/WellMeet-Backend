package com.wellmeet.member.repository;

import com.wellmeet.member.domain.Member;
import com.wellmeet.member.domain.MemberRestaurant;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRestaurantRepository extends JpaRepository<MemberRestaurant, Long> {

    List<MemberRestaurant> findByMemberId(Long memberId);

    Optional<MemberRestaurant> findByMemberAndRestaurant(Member member, Restaurant restaurant);

    boolean existsByMemberIdAndRestaurantId(Long memberId, Long restaurantId);
}
