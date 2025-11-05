package com.wellmeet.restaurant.dto;

import com.wellmeet.common.dto.AvailableDateDTO;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AvailableDateResponse {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private int capacity;
    private boolean available;

    public AvailableDateResponse(AvailableDateDTO availableDate) {
        this.id = availableDate.id();
        this.date = availableDate.date();
        this.time = availableDate.time();
        this.capacity = availableDate.maxCapacity();
        this.available = availableDate.isAvailable();
    }
}
