package com.wellmeet.restaurant.service.menu.service;

import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.restaurant.repository.menu.repository.MenuRepository;
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
