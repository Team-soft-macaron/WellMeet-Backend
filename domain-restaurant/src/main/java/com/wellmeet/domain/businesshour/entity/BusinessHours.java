package com.wellmeet.domain.businesshour.entity;

import java.util.Comparator;
import java.util.List;
import lombok.Getter;

@Getter
public class BusinessHours {

    private final List<BusinessHour> value;

    public BusinessHours(List<BusinessHour> businessHours) {
        this.value = businessHours
                .stream()
                .sorted(Comparator.comparingInt(b -> b.getDayOfWeek().ordinal()))
                .toList();
    }
}
