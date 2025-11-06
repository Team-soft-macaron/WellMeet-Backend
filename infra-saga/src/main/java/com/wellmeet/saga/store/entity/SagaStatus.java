package com.wellmeet.saga.store.entity;

public enum SagaStatus {

    IN_PROGRESS,    // 진행 중
    COMPLETED,      // 완료
    COMPENSATING,   // 보상 중
    COMPENSATED,    // 보상 완료
    FAILED          // 실패
}
