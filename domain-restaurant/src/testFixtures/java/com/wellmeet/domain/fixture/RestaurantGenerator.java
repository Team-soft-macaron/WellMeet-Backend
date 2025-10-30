package com.wellmeet.domain.fixture;

import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class RestaurantGenerator {

    private final RestaurantRepository restaurantRepository;

    public RestaurantGenerator(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant generate(String name, String ownerId) {
        Restaurant restaurant = new Restaurant(UUID.randomUUID().toString(), name, "address", 32.1, 37.1, "thumbnail",
                ownerId);
        return restaurantRepository.save(restaurant);
    }

    public Restaurant generate(String name, double latitude, double longitude, String ownerId) {
        Restaurant restaurant = new Restaurant(UUID.randomUUID().toString(), name, "address", latitude, longitude,
                "thumbnail",
                ownerId);
        return restaurantRepository.save(restaurant);
    }
}
