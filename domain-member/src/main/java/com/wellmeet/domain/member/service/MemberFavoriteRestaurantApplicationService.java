package com.wellmeet.domain.member.service;

import com.wellmeet.common.dto.FavoriteRestaurantDTO;
import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
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

    public List<FavoriteRestaurantDTO> getFavoritesByMemberId(String memberId) {
        return favoriteRestaurantDomainService.findAllByMemberId(memberId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public FavoriteRestaurantDTO addFavorite(String memberId, String restaurantId) {
        FavoriteRestaurant favoriteRestaurant = new FavoriteRestaurant(memberId, restaurantId);
        favoriteRestaurantDomainService.save(favoriteRestaurant);
        return toDTO(favoriteRestaurant);
    }

    @Transactional
    public void removeFavorite(String memberId, String restaurantId) {
        FavoriteRestaurant favoriteRestaurant =
                favoriteRestaurantDomainService.getByMemberIdAndRestaurantId(memberId, restaurantId);
        favoriteRestaurantDomainService.delete(favoriteRestaurant);
    }

    private FavoriteRestaurantDTO toDTO(FavoriteRestaurant favoriteRestaurant) {
        return new FavoriteRestaurantDTO(
                favoriteRestaurant.getId(),
                favoriteRestaurant.getMemberId(),
                favoriteRestaurant.getRestaurantId(),
                favoriteRestaurant.getCreatedAt(),
                favoriteRestaurant.getUpdatedAt()
        );
    }
}
