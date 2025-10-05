package com.wellmeet.kafka.dto.payload;

import com.wellmeet.kafka.dto.NotificationPayload;
import com.wellmeet.kafka.dto.NotificationType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ReservationCreatedPayload extends NotificationPayload {

    private final Long reservationId;
    private final String customerName;
    private final LocalDateTime reservationTime;
    private final int partySize;

    @Override
    public NotificationType getType() {
        return NotificationType.RESERVATION_CREATED;
    }
}
