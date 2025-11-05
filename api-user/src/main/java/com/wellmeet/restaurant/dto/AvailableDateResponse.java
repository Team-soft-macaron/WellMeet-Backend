package com.wellmeet.restaurant.dto;

import com.wellmeet.client.dto.AvailableDateDTO;
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
        this.id = availableDate.getId();
        this.date = availableDate.getDate();
        this.time = availableDate.getTime();
        this.capacity = availableDate.getMaxCapacity();
        this.available = availableDate.isAvailable();
    }
}
