package com.wellmeet.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBookingRequest {

    @Future(message = "예약 시간은 현재 시간 이후여야 합니다.")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateTime;

    @Min(value = 1, message = "인원은 1명 이상이어야 합니다.")
    private Integer party;

    private Integer tableNumber;

    private String note;
}
