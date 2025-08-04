package com.wellmeet.fixture;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import org.springframework.stereotype.Component;

@Component
public class ReservationGenerator {

    private final ReservationRepository reservationRepository;

    public ReservationGenerator(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation generate(Restaurant restaurant, AvailableDate availableDate, Member member, int partySize) {
        Reservation reservation = new Reservation(restaurant, availableDate, member, partySize, "request");
        return reservationRepository.save(reservation);
    }
}