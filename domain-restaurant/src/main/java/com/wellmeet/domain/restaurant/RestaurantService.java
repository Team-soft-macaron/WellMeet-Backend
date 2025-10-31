package com.wellmeet.domain.restaurant;

import com.wellmeet.domain.restaurant.dto.RestaurantResponse;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public RestaurantResponse getRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));

        return RestaurantResponse.from(restaurant);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    public List<RestaurantResponse> getRestaurantsByIds(List<String> restaurantIds) {
        return restaurantRepository.findAllByIdIn(restaurantIds)
                .stream()
                .map(RestaurantResponse::from)
                .toList();
    }
}
