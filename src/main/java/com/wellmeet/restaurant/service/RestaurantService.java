package com.wellmeet.restaurant.service;

import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.restaurant.domain.BoundingBox;
import com.wellmeet.restaurant.domain.Restaurant;
import com.wellmeet.restaurant.domain.crawlingreview.domain.VibeName;
import com.wellmeet.restaurant.dto.MenuResponse;
import com.wellmeet.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.restaurant.dto.RestaurantResponse;
import com.wellmeet.restaurant.dto.ReviewResponse;
import com.wellmeet.restaurant.repository.RestaurantRepository;
import com.wellmeet.restaurant.service.menu.service.MenuService;
import com.wellmeet.restaurant.service.review.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewService reviewService;
    private final MenuService menuService;

    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            VibeName vibeName, double latitude, double longitude
    ) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findRestaurantsOrderedByVibeRatioWithBoundBox(vibeName, boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }


    public List<RecommendRestaurantResponse> getNearbyRestaurants(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findWithBoundBox(boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }

    public RestaurantResponse getRestaurant(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
        List<ReviewResponse> reviews = reviewService.findByRestaurantId(restaurant.getId());
        List<MenuResponse> menus = menuService.findByRestaurantId(restaurant.getId());
        return new RestaurantResponse(restaurant, reviews, menus);
    }
}
