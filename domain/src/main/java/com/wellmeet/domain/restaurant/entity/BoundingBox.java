package com.wellmeet.domain.restaurant.entity;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import lombok.Getter;

@Getter
public class BoundingBox {

    private static final double RADIUS = 5.0;
    private static final double DEGREES_TO_KM = 111.0;
    protected static final double MINIMUM_LATITUDE = -90.0;
    protected static final double MAXIMUM_LATITUDE = 90.0;
    protected static final double MINIMUM_LONGITUDE = -180.0;
    protected static final double MAXIMUM_LONGITUDE = 180.0;

    private final double minLatitude;
    private final double maxLatitude;
    private final double minLongitude;
    private final double maxLongitude;

    public BoundingBox(double latitude, double longitude) {
        if (latitude < MINIMUM_LATITUDE || latitude > MAXIMUM_LATITUDE) {
            throw new WellMeetDomainException(DomainErrorCode.INVALID_LATITUDE);
        }
        if (longitude < MINIMUM_LONGITUDE || longitude > MAXIMUM_LONGITUDE) {
            throw new WellMeetDomainException(DomainErrorCode.INVALID_LONGITUDE);
        }

        double latitudeDelta = RADIUS / DEGREES_TO_KM;
        double longitudeDelta = RADIUS / (DEGREES_TO_KM * Math.cos(Math.toRadians(latitude)));

        this.minLatitude = latitude - latitudeDelta;
        this.maxLatitude = latitude + latitudeDelta;
        this.minLongitude = longitude - longitudeDelta;
        this.maxLongitude = longitude + longitudeDelta;
    }
}
