package com.wellmeet.domain.restaurant.businesshour.entity;

import com.wellmeet.domain.common.BaseEntity;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.domain.restaurant.exception.RestaurantErrorCode;
import com.wellmeet.domain.restaurant.exception.RestaurantException;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessHour extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Enumerated(value = EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private boolean isOpen;
    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime breakStartTime;
    private LocalTime breakEndTime;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    public BusinessHour(DayOfWeek dayOfWeek, boolean isOpen, LocalTime openTime, LocalTime closeTime,
                        LocalTime breakStartTime, LocalTime breakEndTime, Restaurant restaurant) {
        validateTime(openTime, closeTime);
        validateTime(breakStartTime, breakEndTime);
        validateBreakTime(openTime, closeTime, breakStartTime, breakEndTime);

        this.dayOfWeek = dayOfWeek;
        this.isOpen = isOpen;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
        this.restaurant = restaurant;
    }

    private void validateTime(LocalTime beforeTime, LocalTime afterTime) {
        if (beforeTime.isAfter(afterTime)) {
            throw new RestaurantException(RestaurantErrorCode.TIME_SEQUENCE_INVALID);
        }
    }

    private void validateBreakTime(LocalTime openTime, LocalTime closeTime, LocalTime breakStartTime,
                                   LocalTime breakEndTime) {
        if (breakStartTime.isBefore(openTime) || breakEndTime.isAfter(closeTime)) {
            throw new RestaurantException(RestaurantErrorCode.TIME_SEQUENCE_INVALID);
        }
    }

    public void updateHour(
            boolean isOpen,
            LocalTime openTime,
            LocalTime closeTime,
            LocalTime breakStartTime,
            LocalTime breakEndTime
    ) {
        validateTime(openTime, closeTime);
        validateTime(breakStartTime, breakEndTime);
        validateBreakTime(openTime, closeTime, breakStartTime, breakEndTime);

        this.isOpen = isOpen;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStartTime = breakStartTime;
        this.breakEndTime = breakEndTime;
    }
}
