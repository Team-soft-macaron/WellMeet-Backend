package com.wellmeet.member.service;

import com.wellmeet.member.domain.MemberRestaurant;
import com.wellmeet.member.repository.MemberRestaurantRepository;
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
}
