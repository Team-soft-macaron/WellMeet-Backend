package com.wellmeet.reservation;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import org.springframework.context.ApplicationEventPublisher;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationDomainService reservationDomainService;
    private final ReservationRedisService reservationRedisService;
    private final RestaurantDomainService restaurantDomainService;
    private final MemberDomainService memberDomainService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(request.getAvailableDateId(),
                request.getRestaurantId());
        reservationDomainService.alreadyReserved(memberId, request.getRestaurantId(), request.getAvailableDateId());
        reservationRedisService.isReserving(memberId, request.getRestaurantId(), request.getAvailableDateId());
        Member member = memberDomainService.getById(memberId);
        restaurantDomainService.decreaseAvailableDateCapacity(availableDate, request.getPartySize());
        Reservation reservation = request.toDomain(availableDate.getRestaurant(), availableDate, member);

        Reservation savedReservation = reservationDomainService.save(reservation);
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                savedReservation.getId(),
                member.getId(),
                savedReservation.getRestaurant().getId(),
                savedReservation.getRestaurant().getName(),
                savedReservation.getStatus().name(),
                savedReservation.getPartySize(),
                savedReservation.getSpecialRequest(),
                savedReservation.getDateTime(),
                savedReservation.getCreatedAt()
        );
        eventPublisher.publishEvent(event);
        return new CreateReservationResponse(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<SummaryReservationResponse> getReservations(String memberId) {
        return reservationDomainService.findAllByMemberId(memberId)
                .stream()
                .map(SummaryReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long reservationId, String memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        double rating = restaurantDomainService.getAverageRating(reservation.getRestaurant().getId());
        return new ReservationResponse(reservation, rating);
    }

    @Transactional
    public CreateReservationResponse updateReservation(
            Long reservationId,
            String memberId,
            CreateReservationRequest request
    ) {
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(request.getAvailableDateId(),
                request.getRestaurantId());
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        reservationRedisService.isUpdating(memberId, reservationId);
        restaurantDomainService.increaseAvailableDateCapacity(reservation.getAvailableDate(),
                reservation.getPartySize());
        restaurantDomainService.decreaseAvailableDateCapacity(availableDate, request.getPartySize());
        reservation.update(
                availableDate,
                request.getPartySize(),
                request.getSpecialRequest()
        );
        return new CreateReservationResponse(reservation);
    }

    @Transactional
    public void cancel(Long reservationId, String memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        AvailableDate availableDate = reservation.getAvailableDate();
        restaurantDomainService.increaseAvailableDateCapacity(availableDate, reservation.getPartySize());
        reservation.cancel();
    }
}
