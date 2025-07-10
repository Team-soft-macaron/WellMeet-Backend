package com.wellmeet.restaurant.model.menu.service;

import com.wellmeet.restaurant.model.menu.repository.MenuRepository;
import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
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
