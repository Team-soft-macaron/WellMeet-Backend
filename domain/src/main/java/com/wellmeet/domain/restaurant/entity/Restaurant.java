package com.wellmeet.domain.restaurant.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    protected static final double MINIMUM_LATITUDE = -90.0;
    protected static final double MAXIMUM_LATITUDE = 90.0;
    protected static final double MINIMUM_LONGITUDE = -180.0;
    protected static final double MAXIMUM_LONGITUDE = 180.0;

    @Id
    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String address;

    private double latitude;
    private double longitude;
    private String thumbnail;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    public Restaurant(String id, String name, String address, double latitude, double longitude, String thumbnail,
                      Owner owner) {
        validatePosition(latitude, longitude);

        this.id = id;
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
        this.owner = owner;
    }

    private void validatePosition(double latitude, double longitude) {
        if (latitude < MINIMUM_LATITUDE || latitude > MAXIMUM_LATITUDE) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_LATITUDE);
        }
        if (longitude < MINIMUM_LONGITUDE || longitude > MAXIMUM_LONGITUDE) {
            throw new RestaurantException(RestaurantErrorCode.INVALID_LONGITUDE);
        }
    }

    public void update(String name, String address, double latitude, double longitude, String thumbnail){
        validatePosition(latitude, longitude);
        
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.thumbnail = thumbnail;
    }
}
