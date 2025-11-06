package com.wellmeet.restaurant;

import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.BusinessHourDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.UpdateOperatingHoursDTO;
import com.wellmeet.common.dto.request.UpdateRestaurantDTO;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerRestaurantBffService {

    private final RestaurantFeignClient restaurantClient;
    private final OwnerEventPublishBffService eventPublishService;

    @Transactional(readOnly = true)
    public OperatingHoursResponse getOperatingHours(String restaurantId) {
        List<BusinessHourDTO> businessHours = restaurantClient.getOperatingHours(restaurantId);
        return new OperatingHoursResponse(businessHours);
    }

    @Transactional
    public OperatingHoursResponse updateOperatingHours(
            String restaurantId,
            UpdateOperatingHoursRequest request
    ) {
        List<UpdateOperatingHoursDTO.DayHoursDTO> dayHoursList = request.getOperatingHours()
                .stream()
                .map(dayHours -> new UpdateOperatingHoursDTO.DayHoursDTO(
                        dayHours.getDayOfWeek().name(),
                        dayHours.isOperating(),
                        dayHours.getOpen(),
                        dayHours.getClose(),
                        dayHours.getBreakStart(),
                        dayHours.getBreakEnd()
                ))
                .toList();

        UpdateOperatingHoursDTO updateDTO = new UpdateOperatingHoursDTO(dayHoursList);

        List<BusinessHourDTO> businessHours = restaurantClient.updateOperatingHours(restaurantId, updateDTO);
        return new OperatingHoursResponse(businessHours);
    }

    @Transactional
    public UpdateRestaurantResponse updateRestaurant(String restaurantId, UpdateRestaurantRequest request) {
        UpdateRestaurantDTO updateDTO = new UpdateRestaurantDTO(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude(),
                request.getThumbnail()
        );

        RestaurantDTO restaurant = restaurantClient.updateRestaurant(restaurantId, updateDTO);
        eventPublishService.publishRestaurantUpdatedEvent(new RestaurantUpdatedEvent(restaurantId));
        return new UpdateRestaurantResponse(restaurant.name(), restaurant.address(), restaurant.latitude(),
                restaurant.longitude(), restaurant.thumbnail());
    }
}
