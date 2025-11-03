package com.wellmeet.client.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {

    private Long id;
    private String status;
    private String restaurantId;
    private Long availableDateId;
    private String memberId;
    private int partySize;
    private String specialRequest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
