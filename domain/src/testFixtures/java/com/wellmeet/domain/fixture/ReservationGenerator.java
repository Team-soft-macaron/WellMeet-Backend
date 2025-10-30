package com.wellmeet.domain.fixture;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import org.springframework.stereotype.Component;

@Component
public class ReservationGenerator {

    private final ReservationRepository reservationRepository;

    public ReservationGenerator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation generate(String restaurantId, Long availableDateId, String memberId, int partySize) {
        Reservation reservation = new Reservation(restaurantId, availableDateId, memberId, partySize, "request");
        return reservationRepository.save(reservation);
    }
}
