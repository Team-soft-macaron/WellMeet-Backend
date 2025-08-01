package com.wellmeet.domain.reservation.repository;

import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    Optional<Reservation> findByIdAndMemberId(Long id, Long memberId);

    List<Reservation> findAllByMemberId(Long memberId);

    List<Reservation> findAllByRestaurantId(String restaurantId);

    @Query("SELECT r FROM Reservation r WHERE r.restaurant.id = :restaurantId " +
           "AND (:date IS NULL OR DATE(r.reservationDateTime) = :date) " +
           "AND (:status IS NULL OR r.status = :status) " +
           "ORDER BY r.reservationDateTime DESC")
    Page<Reservation> findByRestaurantIdWithFilters(
            @Param("restaurantId") String restaurantId,
            @Param("date") LocalDate date,
            @Param("status") ReservationStatus status,
            Pageable pageable);

    @Query("SELECT r FROM Reservation r JOIN Member m ON r.memberId = m.id " +
           "WHERE r.restaurant.id = :restaurantId " +
           "AND (LOWER(m.name) LIKE LOWER(:query) " +
           "OR LOWER(r.specialRequest) LIKE LOWER(:query)) " +
           "ORDER BY r.reservationDateTime DESC")
    List<Reservation> findByRestaurantIdAndSearchQuery(
            @Param("restaurantId") String restaurantId,
            @Param("query") String query);

    List<Reservation> findByRestaurantIdAndReservationDateTimeBetween(
            String restaurantId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Reservation> findByRestaurantIdAndMemberIdAndReservationDateTimeBefore(
            String restaurantId, Long memberId, LocalDateTime beforeDate);
}
