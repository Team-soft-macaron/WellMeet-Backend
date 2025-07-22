package com.wellmeet.favorite;

import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import com.wellmeet.member.domain.Member;
import com.wellmeet.member.domain.MemberRestaurant;
import com.wellmeet.member.service.MemberRestaurantService;
import com.wellmeet.member.service.MemberService;
import com.wellmeet.restaurant.RestaurantService;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.model.review.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final MemberService memberService;
    private final MemberRestaurantService memberRestaurantService;
    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(Long memberId) {
        return memberRestaurantService.findByMemberId(memberId)
                .stream()
                .map(memberRestaurant -> getFavoriteRestaurantResponse(memberRestaurant.getRestaurant()))
                .toList();
    }

    private FavoriteRestaurantResponse getFavoriteRestaurantResponse(Restaurant restaurant) {
        double rating = reviewService.getAverageRating(restaurant.getId());
        return new FavoriteRestaurantResponse(restaurant, rating);
    }

    public FavoriteRestaurantResponse addFavoriteRestaurant(Long memberId, Long restaurantId) {
        Member member = memberService.getById(memberId);
        Restaurant restaurant = restaurantService.getById(restaurantId);
        MemberRestaurant memberRestaurant = new MemberRestaurant(member, restaurant);
        memberRestaurantService.save(memberRestaurant);
        return getFavoriteRestaurantResponse(restaurant);
    }

    public void removeFavoriteRestaurant(Long memberId, Long restaurantId) {
        Member member = memberService.getById(memberId);
        Restaurant restaurant = restaurantService.getById(restaurantId);
        MemberRestaurant memberRestaurant = memberRestaurantService.getByMemberAndRestaurant(member, restaurant);
        memberRestaurantService.delete(memberRestaurant);
    }
}
