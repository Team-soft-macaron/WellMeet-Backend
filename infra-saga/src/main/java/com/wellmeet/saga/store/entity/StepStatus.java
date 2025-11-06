package com.wellmeet.saga.store.entity;

public enum StepStatus {
    PENDING,        // 대기 중
    RUNNING,        // 실행 중
    SUCCESS,        // 성공
    FAILED,         // 실패
    COMPENSATING,   // 보상 중
    COMPENSATED     // 보상 완료
}
