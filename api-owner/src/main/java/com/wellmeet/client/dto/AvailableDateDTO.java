package com.wellmeet.client.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailableDateDTO {

    private Long id;
    private LocalDate date;
    private LocalTime time;
    private int maxCapacity;
    private boolean isAvailable;
    private String restaurantId;
}
