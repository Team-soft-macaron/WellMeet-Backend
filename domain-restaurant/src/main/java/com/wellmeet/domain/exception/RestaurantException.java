package com.wellmeet.domain.exception;

import lombok.Getter;

@Getter
public class RestaurantException extends RuntimeException {

    private final int statusCode;

    public RestaurantException(RestaurantErrorCode errorCode) {
        super(errorCode.getMessage());
        this.statusCode = errorCode.getStatusCode();
    }
}
