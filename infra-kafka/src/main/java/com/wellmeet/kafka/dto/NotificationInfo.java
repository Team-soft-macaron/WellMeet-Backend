package com.wellmeet.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationInfo {

    private final NotificationType type;
    private final String recipient;
}
