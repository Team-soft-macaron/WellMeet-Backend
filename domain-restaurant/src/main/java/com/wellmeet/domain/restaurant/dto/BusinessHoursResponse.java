package com.wellmeet.domain.restaurant.dto;

import com.wellmeet.domain.restaurant.businesshour.entity.BusinessHours;
import java.util.List;

public record BusinessHoursResponse(
        List<BusinessHourResponse> businessHours
) {
    public static BusinessHoursResponse from(BusinessHours businessHours) {
        List<BusinessHourResponse> businessHourResponses = businessHours.getValue().stream()
                .map(BusinessHourResponse::from)
                .toList();

        return new BusinessHoursResponse(businessHourResponses);
    }
}
