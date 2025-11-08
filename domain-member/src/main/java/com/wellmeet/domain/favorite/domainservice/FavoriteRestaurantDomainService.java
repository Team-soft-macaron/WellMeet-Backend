package com.wellmeet.domain.favorite.domainservice;

import com.wellmeet.domain.favorite.entity.FavoriteRestaurant;
import com.wellmeet.domain.exception.MemberErrorCode;
import com.wellmeet.domain.exception.MemberException;
import com.wellmeet.domain.favorite.repository.FavoriteRestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteRestaurantDomainService {

    private final FavoriteRestaurantRepository favoriteRestaurantRepository;

    public boolean isFavorite(String memberId, String restaurantId) {
        return favoriteRestaurantRepository.existsByMemberIdAndRestaurantId(memberId, restaurantId);
    }

    public List<FavoriteRestaurant> findAllByMemberId(String memberId) {
        return favoriteRestaurantRepository.findByMemberId(memberId);
    }

    public void save(FavoriteRestaurant favoriteRestaurant) {
        favoriteRestaurantRepository.save(favoriteRestaurant);
    }

    public FavoriteRestaurant getByMemberIdAndRestaurantId(String memberId, String restaurantId) {
        return favoriteRestaurantRepository.findByMemberIdAndRestaurantId(memberId, restaurantId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_RESTAURANT_NOT_FOUND));
    }

    public void delete(FavoriteRestaurant favoriteRestaurant) {
        favoriteRestaurantRepository.delete(favoriteRestaurant);
    }
}
