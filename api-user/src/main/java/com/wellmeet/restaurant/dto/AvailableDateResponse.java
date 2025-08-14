package com.wellmeet.restaurant.dto;

import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
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
    private boolean available;

    public AvailableDateResponse(AvailableDate availableDate) {
        this.id = availableDate.getId();
        this.date = availableDate.getDate();
        this.time = availableDate.getTime();
        this.available = availableDate.isAvailable();
    }
}
