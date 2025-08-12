package com.wellmeet.restaurant;

import com.wellmeet.common.util.DistanceCalculator;
import com.wellmeet.domain.member.FavoriteRestaurantDomainService;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.restaurant.dto.AvailableDateResponse;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantDomainService restaurantDomainService;
    private final FavoriteRestaurantDomainService favoriteRestaurantDomainService;

    @Transactional(readOnly = true)
    public List<NearbyRestaurantResponse> findWithNearbyRestaurant(double latitude, double longitude) {
        return restaurantDomainService.findWithBoundBox(latitude, longitude)
                .stream()
                .map(restaurant -> getNearbyRestaurantResponse(restaurant, latitude, longitude))
                .toList();
    }

    private NearbyRestaurantResponse getNearbyRestaurantResponse(Restaurant restaurant, double latitude,
                                                                 double longitude) {
        double rating = restaurantDomainService.getAverageRating(restaurant.getId());
        double distance = DistanceCalculator.calculateDistance(latitude, longitude, restaurant.getLatitude(),
                restaurant.getLongitude());
        return new NearbyRestaurantResponse(restaurant, distance, rating);
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant(String restaurantId, String memberId) {
        boolean isFavorite = favoriteRestaurantDomainService.isFavorite(memberId, restaurantId);
        Restaurant restaurant = restaurantDomainService.getById(restaurantId);
        List<RepresentativeReviewResponse> reviews = restaurantDomainService.getReviewByRestaurantId(restaurant.getId())
                .stream()
                .map(RepresentativeReviewResponse::new)
                .toList();
        List<RepresentativeMenuResponse> menus = restaurantDomainService.getMenuByRestaurantId(restaurant.getId())
                .stream()
                .map(RepresentativeMenuResponse::new)
                .toList();
        double rating = restaurantDomainService.getAverageRating(restaurant.getId());
        return new RestaurantResponse(restaurant, reviews, menus, isFavorite, rating);
    }

    @Transactional(readOnly = true)
    public List<AvailableDateResponse> getRestaurantAvailableDates(String restaurantId) {
        return restaurantDomainService.getRestaurantAvailableDates(restaurantId)
                .stream()
                .map(AvailableDateResponse::new)
                .toList();
    }
}
