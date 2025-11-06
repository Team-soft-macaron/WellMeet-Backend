package com.wellmeet.domain.restaurant;

import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantApplicationService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantApplicationService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public RestaurantDTO getRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        return toDTO(restaurant);
    }

    public List<RestaurantDTO> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<RestaurantDTO> getRestaurantsByIds(List<String> restaurantIds) {
        return restaurantRepository.findAllByIdIn(restaurantIds)
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
