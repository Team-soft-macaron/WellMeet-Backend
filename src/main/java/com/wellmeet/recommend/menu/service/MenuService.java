package com.wellmeet.recommend.menu.service;

import com.wellmeet.recommend.menu.repository.MenuRepository;
import com.wellmeet.recommend.restaurant.dto.RepresentativeMenuResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<RepresentativeMenuResponse> findByRestaurantId(Long restaurantId) {
        return menuRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(RepresentativeMenuResponse::new)
                .toList();
    }
}
