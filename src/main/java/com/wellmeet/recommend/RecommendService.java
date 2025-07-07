package com.wellmeet.recommend;

import com.wellmeet.member.service.MemberRestaurantService;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.menu.service.MenuService;
import com.wellmeet.recommend.restaurant.domain.BoundingBox;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import com.wellmeet.recommend.restaurant.dto.RecommendRestaurantResponse;
import com.wellmeet.recommend.restaurant.dto.RepresentativeMenuResponse;
import com.wellmeet.recommend.restaurant.dto.RepresentativeReviewResponse;
import com.wellmeet.recommend.restaurant.dto.RestaurantResponse;
import com.wellmeet.recommend.restaurant.service.RestaurantService;
import com.wellmeet.recommend.review.service.ReviewService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendService {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;
    private final MenuService menuService;
    private final MemberRestaurantService memberRestaurantService;

    public List<RecommendRestaurantResponse> getRecommendRestaurants(
            VibeName vibeName, double latitude, double longitude
    ) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantService.findRestaurantsOrderedByVibeRatioWithBoundBox(vibeName, boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }

    public List<RecommendRestaurantResponse> getNearbyRestaurants(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantService.findWithBoundBox(boundingBox)
                .stream()
                .map(RecommendRestaurantResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public RestaurantResponse getRestaurant(Long id, Long memberId) {
        boolean isFavorite = memberRestaurantService.isFavorite(memberId, id);
        Restaurant restaurant = restaurantService.getById(id);
        List<RepresentativeReviewResponse> reviews = reviewService.findByRestaurantId(restaurant.getId());
        List<RepresentativeMenuResponse> menus = menuService.findByRestaurantId(restaurant.getId());
        return new RestaurantResponse(restaurant, reviews, menus, isFavorite);
    }
}
