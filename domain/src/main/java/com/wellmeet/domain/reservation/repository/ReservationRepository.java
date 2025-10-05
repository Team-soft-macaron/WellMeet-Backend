package com.wellmeet.domain.reservation.repository;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT r FROM Reservation r " +
            "JOIN FETCH r.availableDate ad " +
            "JOIN FETCH r.restaurant " +
            "JOIN FETCH r.member " +
            "WHERE r.status = :status " +
            "AND (ad.date > :startDate OR (ad.date = :startDate AND ad.time >= :startTime)) " +
            "AND (ad.date < :endDate OR (ad.date = :endDate AND ad.time <= :endTime))",
            countQuery = "SELECT count(r) FROM Reservation r " +
                    "JOIN r.availableDate ad " +
                    "WHERE r.status = :status " +
                    "AND (ad.date > :startDate OR (ad.date = :startDate AND ad.time >= :startTime)) " +
                    "AND (ad.date < :endDate OR (ad.date = :endDate AND ad.time <= :endTime))")
    Page<Reservation> findReservationsForReminderPage(
            @Param("status") ReservationStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("startTime") LocalTime startTime,
            @Param("endDate") LocalDate endDate,
            @Param("endTime") LocalTime endTime,
            Pageable pageable
    );
}
