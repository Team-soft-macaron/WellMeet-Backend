package com.wellmeet.member.repository;

import com.wellmeet.member.domain.MemberRestaurant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRestaurantRepository extends JpaRepository<MemberRestaurant, Long> {

    List<MemberRestaurant> findByMemberId(Long memberId);
}
