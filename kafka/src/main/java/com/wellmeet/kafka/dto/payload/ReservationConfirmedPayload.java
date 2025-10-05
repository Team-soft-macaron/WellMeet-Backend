package com.wellmeet.kafka.dto.payload;

import com.wellmeet.kafka.dto.NotificationPayload;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationConfirmedPayload extends NotificationPayload {

    private final Long reservationId;
    private final String customerName;
    private final LocalDateTime reservationTime;
    private final int partySize;
}
