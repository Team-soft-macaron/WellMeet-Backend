package com.wellmeet.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadAllNotificationsResponse {
    private String message;           // "모든 알림을 읽음 처리했습니다."
    private int updatedCount;         // 업데이트된 알림 수
}