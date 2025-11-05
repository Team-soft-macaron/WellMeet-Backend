package com.wellmeet.domain.member.service;

import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
import com.wellmeet.domain.member.dto.FavoriteRestaurantResponse;
import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberFavoriteRestaurantApplicationService {

    private final FavoriteRestaurantDomainService favoriteRestaurantDomainService;

    public boolean isFavorite(String memberId, String restaurantId) {
        return favoriteRestaurantDomainService.isFavorite(memberId, restaurantId);
    }

    public List<FavoriteRestaurantResponse> getFavoritesByMemberId(String memberId) {
        return favoriteRestaurantDomainService.findAllByMemberId(memberId)
                .stream()
                .map(FavoriteRestaurantResponse::from)
                .toList();
    }

    @Transactional
    public FavoriteRestaurantResponse addFavorite(String memberId, String restaurantId) {
        FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(memberId, restaurantId);
        favoriteRestaurantDomainService.save(favoriteRestaurant);
        return FavoriteRestaurantResponse.from(favoriteRestaurant);
    }

    @Transactional
    public void removeFavorite(String memberId, String restaurantId) {
        FavoriteRestaurant favoriteRestaurant =
                favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(memberId, restaurantId);
        favoriteRestaurantDomainService.delete(favoriteRestaurant);
    }
}
