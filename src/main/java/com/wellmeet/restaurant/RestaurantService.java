package com.wellmeet.restaurant;

import com.wellmeet.common.util.DistanceCalculator;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.member.service.MemberRestaurantService;
import com.wellmeet.restaurant.domain.BoundingBox;
import com.wellmeet.restaurant.domain.PremiumOption;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.dto.NearbyRestaurantResponse;
import com.wellmeet.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import com.wellmeet.restaurant.model.menu.service.MenuService;
import com.wellmeet.restaurant.model.review.service.ReviewService;
import com.wellmeet.restaurant.repository.PremiumOptionRepository;
import com.wellmeet.restaurant.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final PremiumOptionRepository premiumOptionRepository;
    private final ReviewService reviewService;
    private final MenuService menuService;
    private final MemberRestaurantService memberRestaurantService;

    public List<NearbyRestaurantResponse> findWithNearbyRestaurant(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findWithBoundBox(boundingBox)
                .stream()
                .map(restaurant -> getNearbyRestaurantResponse(restaurant, latitude, longitude))
                .toList();
    }

    private NearbyRestaurantResponse getNearbyRestaurantResponse(Restaurant restaurant, double latitude,
                                                                 double longitude) {
        double rating = reviewService.getAverageRating(restaurant.getId());
        double distance = DistanceCalculator.calculateDistance(latitude, longitude, restaurant.getLatitude(),
                restaurant.getLongitude());
        return new NearbyRestaurantResponse(restaurant, distance, rating);
    }

    public Restaurant getById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    public RestaurantResponse getRestaurant(String id, Long memberId) {
        boolean isFavorite = memberRestaurantService.isFavorite(memberId, id);
        Restaurant restaurant = getById(id);
        List<RepresentativeReviewResponse> reviews = reviewService.findByRestaurantId(restaurant.getId());
        List<RepresentativeMenuResponse> menus = menuService.findByRestaurantId(restaurant.getId());
        double rating = reviewService.getAverageRating(restaurant.getId());
        return new RestaurantResponse(restaurant, reviews, menus, isFavorite, rating);
    }

    public PremiumOption getOptionByRestaurantAndOptionId(Restaurant restaurant, Long optionId) {
        return premiumOptionRepository.findByRestaurantAndId(restaurant, optionId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.PREMIUM_OPTION_NOT_FOUND));
    }

    public double getAverageRating(String restaurantId) {
        return reviewService.getAverageRating(restaurantId);
    }
}
