package com.wellmeet.recommend.restaurant.service;

import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.recommend.crawlingreview.domain.VibeName;
import com.wellmeet.recommend.restaurant.domain.BoundingBox;
import com.wellmeet.recommend.restaurant.domain.Restaurant;
import com.wellmeet.recommend.restaurant.repository.RestaurantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<Restaurant> findRestaurantsOrderedByVibeRatioWithBoundBox(VibeName vibeName, BoundingBox boundingBox) {
        return restaurantRepository.findRestaurantsOrderedByVibeRatioWithBoundBox(vibeName, boundingBox);
    }

    public List<Restaurant> findWithBoundBox(BoundingBox boundingBox) {
        return restaurantRepository.findWithBoundBox(boundingBox);
    }

    public Restaurant getById(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new WellMeetException(ErrorCode.RESTAURANT_NOT_FOUND));
    }
}
