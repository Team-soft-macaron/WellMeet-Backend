package com.wellmeet.member.service;

import com.wellmeet.domain.member.domain.Member;
import com.wellmeet.domain.member.domain.MemberRestaurant;
import com.wellmeet.domain.member.repository.MemberRestaurantRepository;
import com.wellmeet.domain.restaurant.domain.Restaurant;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberRestaurantService {

    private final MemberRestaurantRepository memberRestaurantRepository;

    public List<MemberRestaurant> findByMemberId(Long memberId) {
        return memberRestaurantRepository.findByMemberId(memberId);
    }

    public void save(MemberRestaurant memberRestaurant) {
        memberRestaurantRepository.save(memberRestaurant);
    }

    public MemberRestaurant getByMemberAndRestaurant(Member member, Restaurant restaurant) {
        return memberRestaurantRepository.findByMemberAndRestaurant(member, restaurant)
                .orElseThrow(() -> new WellMeetException(ErrorCode.MEMBER_RESTAURANT_NOT_FOUND));
    }

    public void delete(MemberRestaurant memberRestaurant) {
        memberRestaurantRepository.delete(memberRestaurant);
    }

    public boolean isFavorite(Long memberId, String restaurantId) {
        return memberRestaurantRepository.existsByMemberIdAndRestaurantId(memberId, restaurantId);
    }
}
