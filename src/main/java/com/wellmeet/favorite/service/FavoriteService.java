package com.wellmeet.favorite.service;

import com.wellmeet.favorite.dto.FavoriteRestaurantRequest;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import com.wellmeet.member.domain.Member;
import com.wellmeet.member.domain.MemberRestaurant;
import com.wellmeet.member.service.MemberRestaurantService;
import com.wellmeet.member.service.MemberService;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import com.wellmeet.recommend.restaurant.service.RestaurantService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final MemberService memberService;
    private final MemberRestaurantService memberRestaurantService;
    private final RestaurantService restaurantService;

    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(Long memberId) {
        return memberRestaurantService.findByMemberId(memberId)
                .stream()
                .map(memberRestaurant -> new FavoriteRestaurantResponse(memberRestaurant.getRestaurant()))
                .toList();
    }

    public FavoriteRestaurantResponse addFavoriteRestaurant(Long memberId, FavoriteRestaurantRequest request) {
        Member member = memberService.getById(memberId);
        Restaurant restaurant = restaurantService.getById(request.getRestaurantId());
        MemberRestaurant memberRestaurant = new MemberRestaurant(member, restaurant);
        memberRestaurantService.save(memberRestaurant);
        return new FavoriteRestaurantResponse(restaurant);
    }
}
