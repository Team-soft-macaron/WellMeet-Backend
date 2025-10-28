package com.wellmeet.domain.member;

import com.wellmeet.domain.member.entity.FavoriteRestaurant;
import com.wellmeet.domain.member.exception.MemberErrorCode;
import com.wellmeet.domain.member.exception.MemberException;
import com.wellmeet.domain.member.repository.FavoriteRestaurantRepository;
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
