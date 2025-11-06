package com.wellmeet.reservation;

import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.ReservationStatus;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.saga.ReservationCreateSagaFactory;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import com.wellmeet.saga.orchestrator.SagaExecutionException;
import com.wellmeet.saga.orchestrator.SagaOrchestrator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReservationSagaBffService {

    private final SagaOrchestrator sagaOrchestrator;
    private final ReservationCreateSagaFactory sagaFactory;
    private final ReservationFeignClient reservationClient;
    private final RestaurantFeignClient restaurantClient;
    private final ReservationRedisService reservationRedisService;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        reservationRedisService.isReserving(memberId, request.getRestaurantId(), request.getAvailableDateId());

        List<ReservationDTO> memberReservations = reservationClient.getReservationsByMember(memberId);
        boolean alreadyReserved = memberReservations.stream()
                .anyMatch(r -> r.restaurantId().equals(request.getRestaurantId())
                        && r.availableDateId().equals(request.getAvailableDateId())
                        && r.status().equals(ReservationStatus.CONFIRMED));
        if (alreadyReserved) {
            throw new IllegalStateException("이미 예약된 날짜입니다.");
        }

        String sagaId = UUID.randomUUID().toString();
        String idempotencyKey = String.format("reservation:create:%s:%s:%s", 
                memberId, request.getRestaurantId(), request.getAvailableDateId());

        ReservationCreateContext createContext = new ReservationCreateContext(
                memberId,
                request.getRestaurantId(),
                request.getAvailableDateId(),
                request.getPartySize(),
                request.getSpecialRequest()
        );

        SagaContext context = SagaContext.builder()
                .sagaId(sagaId)
                .idempotencyKey(idempotencyKey)
                .put("createContext", createContext)
                .build();

        SagaDefinition<String> saga = sagaFactory.createSaga();

        try {
            sagaOrchestrator.execute(saga, context);

            ReservationDTO reservation = (ReservationDTO) context.getData().get("reservation");
            RestaurantDTO restaurant = restaurantClient.getRestaurant(request.getRestaurantId());
            AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                    request.getRestaurantId(), 
                    request.getAvailableDateId()
            );

            return new CreateReservationResponse(reservation, restaurant.name(), availableDate);

        } catch (SagaExecutionException e) {
            log.error("Saga execution failed: sagaId={}, error={}", sagaId, e.getMessage());
            throw new RuntimeException("예약 처리 중 오류가 발생했습니다.", e);
        }
    }
}
