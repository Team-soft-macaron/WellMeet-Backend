package com.wellmeet.reservation.repository;

import com.wellmeet.reservation.domain.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndMemberId(Long id, Long memberId);

    List<Reservation> findAllByMemberId(Long memberId);
}
