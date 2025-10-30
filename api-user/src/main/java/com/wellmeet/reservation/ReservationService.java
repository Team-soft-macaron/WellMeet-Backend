package com.wellmeet.reservation;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
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
    private final EventPublishService eventPublishService;

    @Transactional
    public CreateReservationResponse reserve(String memberId, CreateReservationRequest request) {
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(request.getAvailableDateId(),
                request.getRestaurantId());
        reservationDomainService.alreadyReserved(memberId, request.getRestaurantId(), request.getAvailableDateId());
        reservationRedisService.isReserving(memberId, request.getRestaurantId(), request.getAvailableDateId());
        Member member = memberDomainService.getById(memberId);
        restaurantDomainService.decreaseAvailableDateCapacity(availableDate, request.getPartySize());
        Reservation reservation = request.toDomain(memberId);

        Reservation savedReservation = reservationDomainService.save(reservation);
        var restaurant = restaurantDomainService.getById(savedReservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                savedReservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationCreatedEvent(event);

        return new CreateReservationResponse(savedReservation, restaurant.getName(), availableDate);
    }

    @Transactional(readOnly = true)
    public List<SummaryReservationResponse> getReservations(String memberId) {
        return reservationDomainService.findAllByMemberId(memberId)
                .stream()
                .map(reservation -> {
                    var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());
                    var availableDate = restaurantDomainService.getAvailableDate(
                            reservation.getAvailableDateId(), reservation.getRestaurantId());
                    return new SummaryReservationResponse(reservation, restaurant.getName(), availableDate);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long reservationId, String memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());
        var availableDate = restaurantDomainService.getAvailableDate(
                reservation.getAvailableDateId(), reservation.getRestaurantId());
        double rating = restaurantDomainService.getAverageRating(reservation.getRestaurantId());
        return new ReservationResponse(reservation, restaurant, availableDate, rating);
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
        if (reservationDomainService.alreadyUpdated(memberId, request.getRestaurantId(), request.getAvailableDateId(),
                request.getPartySize())) {
            var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());
            var currentAvailableDate = restaurantDomainService.getAvailableDate(
                    reservation.getAvailableDateId(), reservation.getRestaurantId());
            return new CreateReservationResponse(reservation, restaurant.getName(), currentAvailableDate);
        }
        AvailableDate oldAvailableDate = restaurantDomainService.getAvailableDate(
                reservation.getAvailableDateId(), reservation.getRestaurantId());
        restaurantDomainService.increaseAvailableDateCapacity(oldAvailableDate,
                reservation.getPartySize());
        restaurantDomainService.decreaseAvailableDateCapacity(availableDate, request.getPartySize());
        reservation.update(
                request.getAvailableDateId(),
                request.getPartySize(),
                request.getSpecialRequest()
        );

        Member member = memberDomainService.getById(memberId);
        var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationUpdatedEvent event = new ReservationUpdatedEvent(
                reservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationUpdatedEvent(event);
        return new CreateReservationResponse(reservation, restaurant.getName(), availableDate);
    }

    @Transactional
    public void cancel(Long reservationId, String memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(
                reservation.getAvailableDateId(), reservation.getRestaurantId());
        restaurantDomainService.increaseAvailableDateCapacity(availableDate, reservation.getPartySize());
        reservation.cancel();

        Member member = memberDomainService.getById(memberId);
        var restaurant = restaurantDomainService.getById(reservation.getRestaurantId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationCanceledEvent event = new ReservationCanceledEvent(
                reservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationCanceledEvent(event);
    }
}
