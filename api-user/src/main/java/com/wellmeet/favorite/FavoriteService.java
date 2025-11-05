package com.wellmeet.favorite;

import com.wellmeet.client.FavoriteRestaurantClient;
import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.FavoriteRestaurantDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.RestaurantIdsRequest;
import com.wellmeet.favorite.dto.FavoriteRestaurantResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRestaurantClient favoriteRestaurantClient;
    private final RestaurantClient restaurantClient;

    public List<FavoriteRestaurantResponse> getFavoriteRestaurants(String memberId) {
        List<FavoriteRestaurantDTO> favoriteRestaurants = favoriteRestaurantClient.getFavoritesByMemberId(memberId);
        if (favoriteRestaurants.isEmpty()) {
            return List.of();
        }

        List<String> restaurantIds = favoriteRestaurants.stream()
                .map(FavoriteRestaurantDTO::restaurantId)
                .toList();

        Map<String, RestaurantDTO> restaurantsById = restaurantClient
                .getRestaurantsByIds(new RestaurantIdsRequest(restaurantIds))
                .stream()
                .collect(Collectors.toMap(RestaurantDTO::getId, Function.identity()));

        return favoriteRestaurants.stream()
                .map(favoriteRestaurant -> {
                    RestaurantDTO restaurant = restaurantsById.get(favoriteRestaurant.restaurantId());
                    return getFavoriteRestaurantResponse(restaurant);
                })
                .toList();
    }

    private FavoriteRestaurantResponse getFavoriteRestaurantResponse(RestaurantDTO restaurant) {
        Double rating = restaurantClient.getAverageRating(restaurant.getId());
        double ratingValue = (rating != null) ? rating : 0.0;
        return new FavoriteRestaurantResponse(restaurant, ratingValue);
    }

    public FavoriteRestaurantResponse addFavoriteRestaurant(String memberId, String restaurantId) {
        RestaurantDTO restaurant = restaurantClient.getRestaurant(restaurantId);
        favoriteRestaurantClient.addFavorite(memberId, restaurantId);
        return getFavoriteRestaurantResponse(restaurant);
    }

    public void removeFavoriteRestaurant(String memberId, String restaurantId) {
        favoriteRestaurantClient.removeFavorite(memberId, restaurantId);
    }
}
