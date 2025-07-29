package com.wellmeet.restaurant.dto;

import com.wellmeet.restaurant.availabledate.domain.AvailableDate;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AvailableDateResponse {

    private LocalDate date;
    private LocalTime time;
    private boolean isAvailable;

    public AvailableDateResponse(AvailableDate availableDate) {
        this.date = availableDate.getDate();
        this.time = availableDate.getTime();
        this.isAvailable = availableDate.isAvailable();
    }
}
