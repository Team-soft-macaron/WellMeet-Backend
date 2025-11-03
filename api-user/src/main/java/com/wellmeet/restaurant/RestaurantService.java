package com.wellmeet.restaurant;

import com.wellmeet.client.AvailableDateClient;
import com.wellmeet.client.FavoriteRestaurantClient;
import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MenuDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.ReviewDTO;
import com.wellmeet.common.util.DistanceCalculator;
import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private static final double SEARCH_RADIUS_KM = 5.0;

    private final RestaurantClient restaurantClient;
    private final FavoriteRestaurantClient favoriteRestaurantClient;
    private final AvailableDateClient availableDateClient;

    public List<NearbyRestaurantResponse> findWithNearbyRestaurant(double latitude, double longitude) {
        return restaurantClient.getAllRestaurants()
                .stream()
                .filter(restaurant -> {
                    double distance = DistanceCalculator.calculateDistance(
                            latitude, longitude,
                            restaurant.getLatitude(), restaurant.getLongitude()
                    );
                    return distance <= SEARCH_RADIUS_KM;
                })
                .map(restaurant -> getNearbyRestaurantResponse(restaurant, latitude, longitude))
                .toList();
    }

    private NearbyRestaurantResponse getNearbyRestaurantResponse(RestaurantDTO restaurant, double latitude,
                                                                 double longitude) {
        Double rating = restaurantClient.getAverageRating(restaurant.getId());
        double ratingValue = (rating != null) ? rating : 0.0;
        double distance = DistanceCalculator.calculateDistance(
                latitude, longitude,
                restaurant.getLatitude(), restaurant.getLongitude()
        );
        return new NearbyRestaurantResponse(restaurant, distance, ratingValue);
    }

    public RestaurantResponse getRestaurant(String restaurantId, String memberId) {
        Boolean isFavorite = favoriteRestaurantClient.isFavorite(memberId, restaurantId);
        boolean isFavoriteValue = (isFavorite != null) ? isFavorite : false;

        RestaurantDTO restaurant = restaurantClient.getRestaurant(restaurantId);

        List<ReviewDTO> reviewDTOs = restaurantClient.getReviewsByRestaurant(restaurant.getId());
        List<RepresentativeReviewResponse> reviews = reviewDTOs.stream()
                .map(RepresentativeReviewResponse::new)
                .toList();

        List<MenuDTO> menuDTOs = restaurantClient.getMenusByRestaurant(restaurant.getId());
        List<RepresentativeMenuResponse> menus = menuDTOs.stream()
                .map(RepresentativeMenuResponse::new)
                .toList();

        Double rating = restaurantClient.getAverageRating(restaurant.getId());
        double ratingValue = (rating != null) ? rating : 0.0;

        return new RestaurantResponse(restaurant, reviews, menus, isFavoriteValue, ratingValue);
    }

    public List<AvailableDateResponse> getRestaurantAvailableDates(String restaurantId) {
        List<AvailableDateDTO> availableDates = availableDateClient.getAvailableDatesByRestaurant(restaurantId);
        return availableDates.stream()
                .map(AvailableDateResponse::new)
                .toList();
    }
}
