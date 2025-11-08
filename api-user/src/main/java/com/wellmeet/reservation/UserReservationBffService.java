package com.wellmeet.reservation;

import com.wellmeet.client.RestaurantAvailableDateFeignClient;
import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.client.dto.request.RestaurantIdsRequest;
import com.wellmeet.client.dto.request.UpdateReservationDTO;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.ReservationStatus;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.CreateReservationDTO;
import com.wellmeet.global.event.UserEventPublishBffService;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import com.wellmeet.reservation.saga.ReservationCancelSagaFactory;
import com.wellmeet.reservation.saga.ReservationCreateSagaFactory;
import com.wellmeet.reservation.saga.ReservationUpdateSagaFactory;
import com.wellmeet.saga.core.SagaContext;
import com.wellmeet.saga.core.SagaDefinition;
import com.wellmeet.saga.orchestrator.ReservationCancelContext;
import com.wellmeet.saga.orchestrator.ReservationCreateContext;
import com.wellmeet.saga.orchestrator.ReservationUpdateContext;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserReservationBffService {

    private final ReservationFeignClient reservationClient;
    private final ReservationRedisService reservationRedisService;
    private final RestaurantFeignClient restaurantClient;
    private final RestaurantAvailableDateFeignClient availableDateClient;
    private final MemberFeignClient memberClient;
    private final UserEventPublishBffService eventPublishService;
    private final SagaOrchestrator sagaOrchestrator;
    private final ReservationCreateSagaFactory sagaFactory;
    private final ReservationUpdateSagaFactory updateSagaFactory;
    private final ReservationCancelSagaFactory cancelSagaFactory;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. Redis 분산 락 획득
        reservationRedisService.isReserving(memberId, request.getRestaurantId(), request.getAvailableDateId());

        // 2. 중복 예약 체크 (BFF에서 직접 처리)
        List<ReservationDTO> memberReservations = reservationClient.getReservationsByMember(memberId);
        boolean alreadyReserved = memberReservations.stream()
                .anyMatch(r -> r.restaurantId().equals(request.getRestaurantId())
                        && r.availableDateId().equals(request.getAvailableDateId())
                        && r.status().equals(ReservationStatus.CONFIRMED));
        if (alreadyReserved) {
            throw new IllegalStateException("이미 예약된 날짜입니다.");
        }

        // 3. Saga 실행 (Capacity 감소 → Reservation 생성 → 이벤트 발행)
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

            // 4. 응답 생성
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

    public List<SummaryReservationResponse> getReservations(String memberId) {
        List<ReservationDTO> reservations = reservationClient.getReservationsByMember(memberId);

        if (reservations.isEmpty()) {
            return List.of();
        }

        List<String> restaurantIds = reservations.stream()
                .map(ReservationDTO::restaurantId)
                .distinct()
                .toList();

        // Restaurant 배치 조회
        Map<String, RestaurantDTO> restaurantsById = restaurantClient
                .getRestaurantsByIds(new RestaurantIdsRequest(restaurantIds))
                .stream()
                .collect(Collectors.toMap(RestaurantDTO::id, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    RestaurantDTO restaurant = restaurantsById.get(reservation.restaurantId());
                    // AvailableDate는 각 Restaurant에서 개별 조회
                    AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                            reservation.restaurantId(),
                            reservation.availableDateId()
                    );
                    return new SummaryReservationResponse(
                            reservation,
                            restaurant.name(),
                            availableDate
                    );
                })
                .toList();
    }

    public ReservationResponse getReservation(Long reservationId, String memberId) {
        ReservationDTO reservation = reservationClient.getReservation(reservationId);

        // memberId 검증 (BFF에서 처리)
        if (!reservation.memberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.restaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.restaurantId(),
                reservation.availableDateId()
        );

        Double rating = restaurantClient.getAverageRating(reservation.restaurantId());
        double ratingValue = (rating != null) ? rating : 0.0;

        return new ReservationResponse(reservation, restaurant, availableDate, ratingValue);
    }

    public CreateReservationResponse updateReservation(
            Long reservationId,
            String memberId,
            CreateReservationRequest request
    ) {
        // 1. Redis 분산 락 획득
        reservationRedisService.isUpdating(memberId, reservationId);

        // 2. 현재 예약 정보 조회
        ReservationDTO reservation = reservationClient.getReservation(reservationId);
        if (!reservation.memberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 3. 중복 수정 체크 (BFF에서 직접 처리)
        boolean alreadyUpdated = reservation.restaurantId().equals(request.getRestaurantId())
                && reservation.availableDateId().equals(request.getAvailableDateId())
                && reservation.partySize() == request.getPartySize();
        if (alreadyUpdated) {
            RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.restaurantId());
            AvailableDateDTO currentAvailableDate = restaurantClient.getAvailableDate(
                    reservation.restaurantId(),
                    reservation.availableDateId()
            );
            return new CreateReservationResponse(reservation, restaurant.name(), currentAvailableDate);
        }

        // 4. Saga 실행 (기존 Capacity 복구 → 새 Capacity 감소 → Reservation 업데이트 → 이벤트 발행)
        String sagaId = UUID.randomUUID().toString();
        String idempotencyKey = String.format("reservation:update:%s:%s",
                reservationId, request.getAvailableDateId());

        ReservationUpdateContext updateContext = new ReservationUpdateContext(
                reservationId,
                memberId,
                reservation.restaurantId(),
                reservation.availableDateId(),
                reservation.partySize(),
                request.getRestaurantId(),
                request.getAvailableDateId(),
                request.getPartySize(),
                request.getSpecialRequest()
        );

        SagaContext context = SagaContext.builder()
                .sagaId(sagaId)
                .idempotencyKey(idempotencyKey)
                .put("updateContext", updateContext)
                .build();

        SagaDefinition<String> saga = updateSagaFactory.createSaga();

        try {
            sagaOrchestrator.execute(saga, context);

            // 5. 응답 생성
            ReservationDTO updatedReservation = (ReservationDTO) context.getData().get("reservation");
            RestaurantDTO restaurant = restaurantClient.getRestaurant(request.getRestaurantId());
            AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                    request.getRestaurantId(),
                    request.getAvailableDateId()
            );

            return new CreateReservationResponse(updatedReservation, restaurant.name(), availableDate);

        } catch (SagaExecutionException e) {
            log.error("Saga execution failed: sagaId={}, error={}", sagaId, e.getMessage());
            throw new RuntimeException("예약 수정 중 오류가 발생했습니다.", e);
        }
    }

    public void cancel(Long reservationId, String memberId) {
        // 1. 예약 정보 조회 및 권한 검증
        ReservationDTO reservation = reservationClient.getReservation(reservationId);
        if (!reservation.memberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 2. Saga 실행 (Capacity 복구 → Reservation 취소 → 이벤트 발행)
        String sagaId = UUID.randomUUID().toString();
        String idempotencyKey = String.format("reservation:cancel:%s", reservationId);

        ReservationCancelContext cancelContext = new ReservationCancelContext(
                reservationId,
                memberId,
                reservation.restaurantId(),
                reservation.availableDateId(),
                reservation.partySize()
        );

        SagaContext context = SagaContext.builder()
                .sagaId(sagaId)
                .idempotencyKey(idempotencyKey)
                .put("cancelContext", cancelContext)
                .put("reservation", reservation)
                .build();

        SagaDefinition<String> saga = cancelSagaFactory.createSaga();

        try {
            sagaOrchestrator.execute(saga, context);
        } catch (SagaExecutionException e) {
            log.error("Saga execution failed: sagaId={}, error={}", sagaId, e.getMessage());
            throw new RuntimeException("예약 취소 중 오류가 발생했습니다.", e);
        }
    }
}
