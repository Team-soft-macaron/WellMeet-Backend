package com.wellmeet.domain.restaurant;

import com.wellmeet.domain.restaurant.availabledate.AvailableDateDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import com.wellmeet.domain.restaurant.menu.MenuDomainService;
import com.wellmeet.domain.restaurant.menu.entity.Menu;
import com.wellmeet.domain.restaurant.model.BoundingBox;
import com.wellmeet.domain.restaurant.repository.RestaurantRepository;
import com.wellmeet.domain.restaurant.review.ReviewDomainService;
import com.wellmeet.domain.restaurant.review.entity.Review;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantDomainService {

    private final RestaurantRepository restaurantRepository;
    private final ReviewDomainService reviewDomainService;
    private final AvailableDateDomainService availableDateDomainService;
    private final MenuDomainService menuDomainService;

    public Restaurant getById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantException(RestaurantErrorCode.RESTAURANT_NOT_FOUND));
    }

    public List<Restaurant> findWithBoundBox(double latitude, double longitude) {
        BoundingBox boundingBox = new BoundingBox(latitude, longitude);
        return restaurantRepository.findWithBoundBox(boundingBox);
    }

    public double getAverageRating(String restaurantId) {
        return reviewDomainService.getAverageRating(restaurantId);
    }

    public List<AvailableDate> getRestaurantAvailableDates(String restaurantId) {
        return availableDateDomainService.getAvailableDatesByRestaurantId(restaurantId);
    }

    public List<Review> getReviewByRestaurantId(String restaurantId) {
        return reviewDomainService.getByRestaurantId(restaurantId);
    }

    public List<Menu> getMenuByRestaurantId(String restaurantId) {
        return menuDomainService.getByRestaurantId(restaurantId);
    }

    public AvailableDate getAvailableDate(Long availableDateId, String restaurantId) {
        return availableDateDomainService.getByIdAndRestaurantId(availableDateId, restaurantId);
    }

    public void decreaseAvailableDateCapacity(AvailableDate availableDate, int partySize) {
        if (availableDate.canNotReserve(partySize)) {
            throw new RestaurantException(RestaurantErrorCode.NOT_ENOUGH_CAPACITY);
        }
        availableDateDomainService.decreaseCapacity(availableDate, partySize);
    }
}
