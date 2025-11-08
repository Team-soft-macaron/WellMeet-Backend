package com.wellmeet.domain.restaurant;

import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.domain.restaurant.domainservice.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantApplicationService {

    private final RestaurantDomainService restaurantDomainService;

    public RestaurantDTO getRestaurantById(String id) {
        Restaurant restaurant = restaurantDomainService.getById(id);

        return toDTO(restaurant);
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantDomainService.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<RestaurantDTO> getRestaurantsByIds(List<String> restaurantIds) {
        return restaurantDomainService.findAllByIdIn(restaurantIds)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private RestaurantDTO toDTO(Restaurant restaurant) {
        return new RestaurantDTO(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getThumbnail(),
                restaurant.getOwnerId(),
                restaurant.getCreatedAt(),
                restaurant.getUpdatedAt()
        );
    }
}
