package com.wellmeet.restaurant.domain;

import lombok.Getter;

@Getter
public class BoundingBox {

    private static final double RADIUS = 5.0;
    private static final double DEGREES_TO_KM = 111.0;

    private final double minLatitude;
    private final double maxLatitude;
    private final double minLongitude;
    private final double maxLongitude;

    public BoundingBox(double latitude, double longitude) {
        double latitudeDelta = RADIUS / DEGREES_TO_KM;
        double longitudeDelta = RADIUS / (DEGREES_TO_KM * Math.cos(Math.toRadians(latitude)));

        this.minLatitude = latitude - latitudeDelta;
        this.maxLatitude = latitude + latitudeDelta;
        this.minLongitude = longitude - longitudeDelta;
        this.maxLongitude = longitude + longitudeDelta;
    }
}
