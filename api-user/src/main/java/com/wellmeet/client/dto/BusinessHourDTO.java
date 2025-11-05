package com.wellmeet.client.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessHourDTO {

    private Long id;
    private String dayOfWeek;
    private boolean isOperating;
    private LocalTime open;
    private LocalTime close;
    private LocalTime breakStart;
    private LocalTime breakEnd;
}