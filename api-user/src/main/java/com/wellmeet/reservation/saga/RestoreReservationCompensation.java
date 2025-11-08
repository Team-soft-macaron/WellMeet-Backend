package com.wellmeet.reservation.saga;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.saga.core.SagaAction;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RestoreReservationCompensation implements SagaAction<Void> {

    private final ReservationFeignClient reservationClient;

    @Override
    public Void execute(SagaContext context) {
        // Note: 취소된 예약을 복구하는 로직
        // 실제로는 ReservationFeignClient에 restoreReservation 메소드가 필요할 수 있음
        // 여기서는 보상 액션의 구조만 정의
        return null;
    }
}
