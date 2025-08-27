package com.wellmeet.kafka.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationInfo {
    private final String type;
    private final String category;
    private final String recipient;
}
