package com.wellmeet.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TerminateSessionsResponse {
    private String message;           // "다른 모든 세션이 종료되었습니다."
    private int terminatedCount;      // 종료된 세션 수
}