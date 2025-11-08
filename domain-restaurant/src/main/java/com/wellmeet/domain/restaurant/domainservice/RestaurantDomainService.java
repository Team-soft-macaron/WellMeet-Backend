package com.wellmeet.domain.restaurant.domainservice;

import com.wellmeet.domain.exception.RestaurantErrorCode;
import com.wellmeet.domain.exception.RestaurantException;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.model.BoundingBox;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantDomainService {

    private final RestaurantRepository restaurantRepository;

    public Restaurant getById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }

    public List<Restaurant> findAll() {
        return restaurantRepository.findAll();
    }

    public List<Restaurant> findAllByIdIn(List<String> restaurantIds) {
        return restaurantRepository.findAllByIdIn(restaurantIds);
    }

    public List<Restaurant> findWithBoundBox(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findWithBoundBox(boundingBox);
    }
}
