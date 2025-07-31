package com.wellmeet.favorite;

import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.review.ReviewDomainService;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRestaurantDomainService favoriteRestaurantDomainService;
    private final ReviewDomainService reviewDomainService;
    private final MemberDomainService memberDomainService;
    private final RestaurantDomainService restaurantDomainService;

    @Transactional(readOnly = true)
    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(Long memberId) {
        return favoriteRestaurantDomainService.findAllByMemberId(memberId)
                .stream()
                .map(favoriteRestaurant -> getFavoriteRestaurantResponse(favoriteRestaurant.getRestaurant()))
                .toList();
    }

    private FavoriteRestaurantResponse getFavoriteRestaurantResponse(Restaurant restaurant) {
        double rating = reviewDomainService.getAverageRating(restaurant.getId());
        return new FavoriteRestaurantResponse(restaurant, rating);
    }

    @Transactional
    public FavoriteRestaurantResponse addFavoriteRestaurant(Long memberId, String restaurantId) {
        Member member = memberDomainService.getById(memberId);
        Restaurant restaurant = restaurantDomainService.getById(restaurantId);
        FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(member, restaurant);
        favoriteRestaurantDomainService.save(favoriteRestaurant);
        return getFavoriteRestaurantResponse(restaurant);
    }

    @Transactional
    public void removeFavoriteRestaurant(Long memberId, String restaurantId) {
        FavoriteRestaurant favoriteRestaurant = favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(memberId,
                restaurantId);
        favoriteRestaurantDomainService.delete(favoriteRestaurant);
    }
}
