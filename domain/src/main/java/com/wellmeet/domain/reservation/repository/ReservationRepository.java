package com.wellmeet.domain.reservation.repository;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndMemberId(Long id, String memberId);

    List<Reservation> findAllByMemberId(String memberId);

    List<Reservation> findAllByRestaurantId(String restaurantId);

    boolean existsByMemberIdAndRestaurantIdAndAvailableDateId(String memberId, String restaurantId,
                                                              Long availableDateId);

    boolean existsByMemberIdAndRestaurantIdAndAvailableDateIdAndPartySize(String memberId, String restaurantId,
                                                                          Long availableDateId, int partySize);

    List<Reservation> findAllByStatusOrderByAvailableDateIdAsc(ReservationStatus status);
}
