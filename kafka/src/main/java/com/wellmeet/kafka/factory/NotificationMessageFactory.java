package com.wellmeet.kafka.factory;

import com.wellmeet.kafka.dto.MessageHeader;
import com.wellmeet.kafka.dto.MessageMetadata;
import com.wellmeet.kafka.dto.NotificationInfo;
import com.wellmeet.kafka.dto.NotificationMessage;
import com.wellmeet.kafka.dto.ReservationCreatedPayload;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class NotificationMessageFactory {

    private static final String MESSAGE_VERSION = "1.0";
    private static final String MESSAGE_SOURCE = "wellmeet-api";
    private static final String ENVIRONMENT = "development";

    public NotificationMessage createReservationCreatedMessage(ReservationCreatedPayload payload) {
        MessageHeader header = new MessageHeader(
                UUID.randomUUID().toString(),
                MESSAGE_VERSION,
                LocalDateTime.now(),
                MESSAGE_SOURCE
        );

        NotificationInfo notification = new NotificationInfo(
                "reservation.created",
                "HIGH",
                "RESTAURANT_OWNER"
        );

        MessageMetadata metadata = new MessageMetadata(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                ENVIRONMENT
        );

        return new NotificationMessage(header, notification, payload, metadata);
    }
}
