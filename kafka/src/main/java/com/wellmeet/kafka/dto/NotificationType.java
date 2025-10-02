package com.wellmeet.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    RESERVATION_CREATED("notification", "wellmeet-user-server");

    private final String topic;
    private final String source;
}
