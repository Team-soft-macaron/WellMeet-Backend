package com.wellmeet.reservation;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.MemberIdsRequest;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.saga.ReservationConfirmContext;
import com.wellmeet.reservation.saga.ReservationConfirmSagaFactory;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.orchestrator.SagaExecutionException;
import com.wellmeet.saga.orchestrator.SagaOrchestrator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OwnerReservationBffService {

    private final ReservationFeignClient reservationClient;
    private final MemberFeignClient memberClient;
    private final RestaurantFeignClient restaurantClient;
    private final OwnerEventPublishBffService eventPublishService;
    private final SagaOrchestrator sagaOrchestrator;
    private final ReservationConfirmSagaFactory confirmSagaFactory;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(String restaurantId) {
        List<ReservationDTO> reservations = reservationClient.getReservationsByRestaurant(restaurantId);
        if (reservations.isEmpty()) {
            return List.of();
        }

        List<String> memberIds = reservations.stream()
                .map(ReservationDTO::memberId)
                .distinct()
                .toList();
        Map<String, MemberDTO> membersById = memberClient.getMembersByIds(
                        new MemberIdsRequest(memberIds))
                .stream()
                .collect(Collectors.toMap(MemberDTO::id, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    MemberDTO member = membersById.get(reservation.memberId());
                    AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                            reservation.restaurantId(), reservation.availableDateId());
                    return new ReservationResponse(
                            reservation,
                            availableDate,
                            member.name(),
                            member.phone(),
                            member.email(),
                            member.isVip()
                    );
                })
                .toList();
    }

    public void confirmReservation(Long reservationId) {
        // Saga 실행 (Reservation 확정 → 이벤트 발행)
        String sagaId = UUID.randomUUID().toString();
        String idempotencyKey = String.format("reservation:confirm:%s", reservationId);

        ReservationConfirmContext confirmContext = new ReservationConfirmContext(reservationId);

        SagaContext context = SagaContext.builder()
                .sagaId(sagaId)
                .idempotencyKey(idempotencyKey)
                .put("confirmContext", confirmContext)
                .build();

        SagaDefinition<String> saga = confirmSagaFactory.createSaga();

        try {
            sagaOrchestrator.execute(saga, context);
        } catch (SagaExecutionException e) {
            log.error("Saga execution failed: sagaId={}, error={}", sagaId, e.getMessage());
            throw new RuntimeException("예약 확정 중 오류가 발생했습니다.", e);
        }
    }
}
