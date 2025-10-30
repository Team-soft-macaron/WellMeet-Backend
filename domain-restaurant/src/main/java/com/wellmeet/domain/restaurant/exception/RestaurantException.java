package com.wellmeet.domain.restaurant.exception;

import com.wellmeet.domain.common.WellMeetDomainException;
import lombok.Getter;

@Getter
public class RestaurantException extends WellMeetDomainException {

    public RestaurantException(RestaurantErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode.getStatusCode());
    }
}
