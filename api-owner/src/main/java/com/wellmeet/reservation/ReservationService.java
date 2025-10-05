package com.wellmeet.reservation;

import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.global.event.EventPublishService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationDomainService reservationDomainService;
    private final EventPublishService eventPublishService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(String restaurantId) {
        return reservationDomainService.findAllByRestaurantId(restaurantId)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        reservation.confirm();

        ReservationConfirmedEvent event = new ReservationConfirmedEvent(reservation);
        eventPublishService.publishReservationConfirmedEvent(event);
    }
}
