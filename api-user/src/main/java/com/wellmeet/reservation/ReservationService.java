package com.wellmeet.reservation;

import com.wellmeet.client.AvailableDateClient;
import com.wellmeet.client.MemberClient;
import com.wellmeet.client.ReservationClient;
import com.wellmeet.client.RestaurantClient;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MemberDTO;
import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.CreateReservationDTO;
import com.wellmeet.client.dto.request.DecreaseCapacityRequest;
import com.wellmeet.client.dto.request.IncreaseCapacityRequest;
import com.wellmeet.client.dto.request.RestaurantIdsRequest;
import com.wellmeet.client.dto.request.UpdateReservationDTO;
import com.wellmeet.global.event.EventPublishService;
import com.wellmeet.global.event.event.ReservationCanceledEvent;
import com.wellmeet.global.event.event.ReservationCreatedEvent;
import com.wellmeet.global.event.event.ReservationUpdatedEvent;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationClient reservationClient;
    private final ReservationRedisService reservationRedisService;
    private final RestaurantClient restaurantClient;
    private final AvailableDateClient availableDateClient;
    private final MemberClient memberClient;
    private final EventPublishService eventPublishService;

    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        // 1. Redis 분산 락 획득
        reservationRedisService.isReserving(memberId, request.getRestaurantId(), request.getAvailableDateId());

        // 2. 중복 예약 체크 (BFF에서 직접 처리)
        List<ReservationDTO> memberReservations = reservationClient.getReservationsByMember(memberId);
        boolean alreadyReserved = memberReservations.stream()
                .anyMatch(r -> r.getRestaurantId().equals(request.getRestaurantId())
                        && r.getAvailableDateId().equals(request.getAvailableDateId())
                        && r.getStatus().equals("CONFIRMED"));
        if (alreadyReserved) {
            throw new IllegalStateException("이미 예약된 날짜입니다.");
        }

        // 3. Member, Restaurant, AvailableDate 조회
        MemberDTO member = memberClient.getMember(memberId);
        RestaurantDTO restaurant = restaurantClient.getRestaurant(request.getRestaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                request.getRestaurantId(), request.getAvailableDateId()
        );

        // 4. Capacity 감소
        availableDateClient.decreaseCapacity(new DecreaseCapacityRequest(
                request.getAvailableDateId(), request.getPartySize()));

        // 5. Reservation 생성
        CreateReservationDTO createRequest = new CreateReservationDTO(
                request.getRestaurantId(),
                request.getAvailableDateId(),
                memberId,
                request.getPartySize(),
                request.getSpecialRequest()
        );
        ReservationDTO savedReservation = reservationClient.createReservation(createRequest);

        // 6. 이벤트 발행
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                savedReservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationCreatedEvent(event);

        return new CreateReservationResponse(savedReservation, restaurant.getName(), availableDate);
    }

    public List<SummaryReservationResponse> getReservations(String memberId) {
        List<ReservationDTO> reservations = reservationClient.getReservationsByMember(memberId);

        if (reservations.isEmpty()) {
            return List.of();
        }

        List<String> restaurantIds = reservations.stream()
                .map(ReservationDTO::getRestaurantId)
                .distinct()
                .toList();

        // Restaurant 배치 조회
        Map<String, RestaurantDTO> restaurantsById = restaurantClient
                .getRestaurantsByIds(new RestaurantIdsRequest(restaurantIds))
                .stream()
                .collect(Collectors.toMap(RestaurantDTO::getId, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    RestaurantDTO restaurant = restaurantsById.get(reservation.getRestaurantId());
                    // AvailableDate는 각 Restaurant에서 개별 조회
                    AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                            reservation.getRestaurantId(),
                            reservation.getAvailableDateId()
                    );
                    return new SummaryReservationResponse(
                            reservation,
                            restaurant.getName(),
                            availableDate
                    );
                })
                .toList();
    }

    public ReservationResponse getReservation(Long reservationId, String memberId) {
        ReservationDTO reservation = reservationClient.getReservation(reservationId);

        // memberId 검증 (BFF에서 처리)
        if (!reservation.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.getRestaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.getRestaurantId(),
                reservation.getAvailableDateId()
        );

        Double rating = restaurantClient.getAverageRating(reservation.getRestaurantId());
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
        if (!reservation.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 3. 중복 수정 체크 (BFF에서 직접 처리)
        boolean alreadyUpdated = reservation.getRestaurantId().equals(request.getRestaurantId())
                && reservation.getAvailableDateId().equals(request.getAvailableDateId())
                && reservation.getPartySize() == request.getPartySize();
        if (alreadyUpdated) {
            RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.getRestaurantId());
            AvailableDateDTO currentAvailableDate = restaurantClient.getAvailableDate(
                    reservation.getRestaurantId(),
                    reservation.getAvailableDateId()
            );
            return new CreateReservationResponse(reservation, restaurant.getName(), currentAvailableDate);
        }

        // 4. 새로운 AvailableDate 조회
        AvailableDateDTO newAvailableDate = restaurantClient.getAvailableDate(
                request.getRestaurantId(),
                request.getAvailableDateId()
        );

        // 5. 보상 트랜잭션: 기존 Capacity 복구 + 새로운 Capacity 감소
        AvailableDateDTO oldAvailableDate = restaurantClient.getAvailableDate(
                reservation.getRestaurantId(),
                reservation.getAvailableDateId()
        );
        availableDateClient.increaseCapacity(new IncreaseCapacityRequest(
                reservation.getAvailableDateId(), reservation.getPartySize()));
        availableDateClient.decreaseCapacity(new DecreaseCapacityRequest(
                request.getAvailableDateId(), request.getPartySize()));

        // 6. Reservation 업데이트
        UpdateReservationDTO updateRequest = new UpdateReservationDTO(
                request.getRestaurantId(),
                request.getAvailableDateId(),
                request.getPartySize(),
                request.getSpecialRequest()
        );
        ReservationDTO updatedReservation = reservationClient.updateReservation(reservationId, updateRequest);

        // 7. 이벤트 발행
        MemberDTO member = memberClient.getMember(memberId);
        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(newAvailableDate.getDate(), newAvailableDate.getTime());
        ReservationUpdatedEvent event = new ReservationUpdatedEvent(
                updatedReservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationUpdatedEvent(event);

        return new CreateReservationResponse(updatedReservation, restaurant.getName(), newAvailableDate);
    }

    public void cancel(Long reservationId, String memberId) {
        // 1. 예약 정보 조회 및 권한 검증
        ReservationDTO reservation = reservationClient.getReservation(reservationId);
        if (!reservation.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 2. AvailableDate 조회
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.getRestaurantId(),
                reservation.getAvailableDateId()
        );

        // 3. 보상 트랜잭션: Capacity 복구
        availableDateClient.increaseCapacity(new IncreaseCapacityRequest(
                reservation.getAvailableDateId(), reservation.getPartySize()));

        // 4. Reservation 취소
        reservationClient.cancelReservation(reservationId);

        // 5. 이벤트 발행
        MemberDTO member = memberClient.getMember(memberId);
        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationCanceledEvent event = new ReservationCanceledEvent(
                reservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationCanceledEvent(event);
    }
}
