package com.wellmeet.restaurant;

import com.wellmeet.common.util.DistanceCalculator;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.member.service.MemberRestaurantService;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.dto.RecommendRestaurantResponse;
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

    // TODO : 리팩토링을 해야 하지만 추천 서버 분리 후 삭제할 예정이므로 우선 보류
    public List<RecommendRestaurantResponse> findNearbyRestaurantsOrderedByVibeRatio(VibeName vibeName,
                                                                                     double latitude,
                                                                                     double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findRestaurantsOrderedByVibeRatioWithBoundBox(vibeName, boundingBox)
                .stream()
                .map(restaurant -> getRecommendRestaurantResponse(restaurant, latitude, longitude))
                .toList();
    }

    // TODO : 리팩토링을 해야 하지만 추천 서버 분리 후 삭제할 예정이므로 우선 보류
    private RecommendRestaurantResponse getRecommendRestaurantResponse(Restaurant restaurant, double latitude,
                                                                       double longitude) {
        double rating = reviewService.getAverageRating(restaurant.getId());
        double distance = DistanceCalculator.calculateDistance(latitude, longitude, restaurant.getLatitude(),
                restaurant.getLongitude());
        return new RecommendRestaurantResponse(restaurant, distance, rating);
    }

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

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
    }

    public RestaurantResponse getRestaurant(Long id, Long memberId) {
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

    public double getAverageRating(Long restaurantId) {
        return reviewService.getAverageRating(restaurantId);
    }
}
