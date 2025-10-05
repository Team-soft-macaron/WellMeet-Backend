package com.wellmeet.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {

    RESERVATION_CREATED("notification"),
    RESERVATION_CONFIRMED("notification"),
    RESERVATION_UPDATED("notification"),
    RESERVATION_CANCELED("notification"),
    ;

    private final String topic;
}
