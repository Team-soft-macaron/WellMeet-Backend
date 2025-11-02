package com.wellmeet.restaurant;

import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.BusinessHourDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.UpdateOperatingHoursDTO;
import com.wellmeet.client.dto.request.UpdateRestaurantDTO;
import com.wellmeet.global.event.EventPublishService;
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
public class RestaurantService {

    private final RestaurantClient restaurantClient;
    private final EventPublishService eventPublishService;

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
                .map(dayHours -> UpdateOperatingHoursDTO.DayHoursDTO.builder()
                        .dayOfWeek(dayHours.getDayOfWeek().name())
                        .isOperating(dayHours.isOperating())
                        .open(dayHours.getOpen())
                        .close(dayHours.getClose())
                        .breakStart(dayHours.getBreakStart())
                        .breakEnd(dayHours.getBreakEnd())
                        .build())
                .toList();

        UpdateOperatingHoursDTO updateDTO = UpdateOperatingHoursDTO.builder()
                .operatingHours(dayHoursList)
                .build();

        List<BusinessHourDTO> businessHours = restaurantClient.updateOperatingHours(restaurantId, updateDTO);
        return new OperatingHoursResponse(businessHours);
    }

    @Transactional
    public UpdateRestaurantResponse updateRestaurant(String restaurantId, UpdateRestaurantRequest request) {
        UpdateRestaurantDTO updateDTO = UpdateRestaurantDTO.builder()
                .name(request.getName())
                .address(request.getAddress())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .thumbnail(request.getThumbnail())
                .build();

        RestaurantDTO restaurant = restaurantClient.updateRestaurant(restaurantId, updateDTO);
        eventPublishService.publishRestaurantUpdatedEvent(new RestaurantUpdatedEvent(restaurantId));
        return new UpdateRestaurantResponse(restaurant.getName(), restaurant.getAddress(), restaurant.getLatitude(),
                restaurant.getLongitude(), restaurant.getThumbnail());
    }
}
