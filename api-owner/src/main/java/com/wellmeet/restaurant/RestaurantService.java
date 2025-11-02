package com.wellmeet.restaurant;

import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.UpdateRestaurantDTO;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHour;
import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import com.wellmeet.domain.restaurant.businesshour.entity.DayOfWeek;
import com.wellmeet.global.event.EventPublishService;
import com.wellmeet.global.event.event.RestaurantUpdatedEvent;
import com.wellmeet.restaurant.dto.OperatingHoursResponse;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest;
import com.wellmeet.restaurant.dto.UpdateOperatingHoursRequest.DayHours;
import com.wellmeet.restaurant.dto.UpdateRestaurantRequest;
import com.wellmeet.restaurant.dto.UpdateRestaurantResponse;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantClient restaurantClient;
    private final RestaurantDomainService restaurantDomainService;
    private final EventPublishService eventPublishService;

    @Transactional(readOnly = true)
    public OperatingHoursResponse getOperatingHours(String restaurantId) {
        BusinessHours operatingHours = restaurantDomainService.getOperatingHours(restaurantId);
        return new OperatingHoursResponse(operatingHours);
    }

    @Transactional
    public OperatingHoursResponse updateOperatingHours(
            String restaurantId,
            UpdateOperatingHoursRequest request
    ) {
        Map<DayOfWeek, UpdateOperatingHoursRequest.DayHours> dayHours = request.getOperatingHours()
                .stream()
                .collect(Collectors.toMap(DayHours::getDayOfWeek, dayHour -> dayHour));
        BusinessHours operatingHours = restaurantDomainService.getOperatingHours(restaurantId);
        operatingHours.getValue()
                .forEach(hour -> updateOperatingHour(hour, dayHours.get(hour.getDayOfWeek())));
        return new OperatingHoursResponse(operatingHours);
    }

    private void updateOperatingHour(BusinessHour hour, DayHours dayHours) {
        hour.updateHour(
                dayHours.isOperating(),
                dayHours.getOpen(),
                dayHours.getClose(),
                dayHours.getBreakStart(),
                dayHours.getBreakEnd()
        );
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
