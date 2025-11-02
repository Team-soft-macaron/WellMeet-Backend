package com.wellmeet.domain.reservation.service;

import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.dto.CreateReservationRequest;
import com.wellmeet.domain.reservation.dto.ReservationResponse;
import com.wellmeet.domain.reservation.dto.UpdateReservationRequest;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReservationApplicationService {

    private final ReservationDomainService reservationDomainService;

    public ReservationApplicationService(ReservationDomainService reservationDomainService) {
        this.reservationDomainService = reservationDomainService;
    }

    @Transactional
    public ReservationResponse createReservation(CreateReservationRequest request) {
        reservationDomainService.alreadyReserved(
                request.memberId(),
                request.restaurantId(),
                request.availableDateId()
        );

        Reservation reservation = new Reservation(
                request.restaurantId(),
                request.availableDateId(),
                request.memberId(),
                request.partySize(),
                request.specialRequest()
        );

        Reservation saved = reservationDomainService.save(reservation);
        return ReservationResponse.from(saved);
    }

    public ReservationResponse getReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        return ReservationResponse.from(reservation);
    }

    public List<ReservationResponse> getReservationsByRestaurant(String restaurantId) {
        List<Reservation> reservations = reservationDomainService.findAllByRestaurantId(restaurantId);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public List<ReservationResponse> getReservationsByMember(String memberId) {
        List<Reservation> reservations = reservationDomainService.findAllByMemberId(memberId);
        return reservations.stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public ReservationResponse updateReservation(Long reservationId, UpdateReservationRequest request) {
        Reservation reservation = reservationDomainService.getById(reservationId);

        if (request.status() == ReservationStatus.CONFIRMED) {
            reservation.confirm();
        }
        if (request.status() == ReservationStatus.CANCELED) {
            reservation.cancel();
        }

        Long availableDateId = reservation.getAvailableDateId();
        int partySize = request.partySize();
        String specialRequest = request.specialRequest();

        reservation.update(availableDateId, partySize, specialRequest);

        Reservation saved = reservationDomainService.save(reservation);
        return ReservationResponse.from(saved);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        reservation.cancel();
        reservationDomainService.save(reservation);
    }
}
