package com.wellmeet.domain.reservation.service;

import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.dto.CreateReservationRequest;
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
    public ReservationDTO createReservation(CreateReservationRequest request) {
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
        return toDTO(saved);
    }

    public ReservationDTO getReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        return toDTO(reservation);
    }

    public List<ReservationDTO> getReservationsByRestaurant(String restaurantId) {
        List<Reservation> reservations = reservationDomainService.findAllByRestaurantId(restaurantId);
        return reservations.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ReservationDTO> getReservationsByMember(String memberId) {
        List<Reservation> reservations = reservationDomainService.findAllByMemberId(memberId);
        return reservations.stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional
    public ReservationDTO updateReservation(Long reservationId, UpdateReservationRequest request) {
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
        return toDTO(saved);
    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        reservation.cancel();
        reservationDomainService.save(reservation);
    }

    private ReservationDTO toDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                convertReservationStatus(reservation.getStatus()),
                reservation.getRestaurantId(),
                reservation.getMemberId(),
                reservation.getAvailableDateId(),
                reservation.getPartySize(),
                reservation.getSpecialRequest(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }

    private com.wellmeet.common.dto.ReservationStatus convertReservationStatus(
            ReservationStatus status
    ) {
        return switch (status) {
            case PENDING -> com.wellmeet.common.dto.ReservationStatus.PENDING;
            case CONFIRMED -> com.wellmeet.common.dto.ReservationStatus.CONFIRMED;
            case CANCELED -> com.wellmeet.common.dto.ReservationStatus.CANCELLED;
        };
    }
}
