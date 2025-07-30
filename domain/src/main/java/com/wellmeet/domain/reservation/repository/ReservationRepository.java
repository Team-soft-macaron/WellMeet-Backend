package com.wellmeet.domain.reservation.repository;

import com.wellmeet.domain.reservation.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndMemberId(Long id, Long memberId);

    List<Reservation> findAllByMemberId(Long memberId);

    List<Reservation> findAllByRestaurantId(String restaurantId);
}
