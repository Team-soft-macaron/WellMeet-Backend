package com.wellmeet.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationMessage {

    private final MessageHeader header;
    private final NotificationInfo notification;
    private final NotificationPayload payload;
}
