package com.wellmeet.fixture;

import com.wellmeet.domain.owner.entity.Owner;
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

    public Restaurant generate(String name, Owner owner) {
        return generate(name, 32.1, 37.1, owner);
    }

    public Restaurant generate(String name, double latitude, double longitude, Owner owner) {
        Restaurant restaurant = new Restaurant(UUID.randomUUID().toString(), name, "address", latitude, longitude,
                "thumbnail",
                owner);
        return restaurantRepository.save(restaurant);
    }
}
