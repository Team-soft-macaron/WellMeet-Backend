package com.wellmeet.domain.reservation;

import com.wellmeet.domain.exception.DomainErrorCode;
import com.wellmeet.domain.exception.WellMeetDomainException;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.entity.SelectedPremiumOption;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import com.wellmeet.domain.reservation.repository.SelectedPremiumOptionRepository;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.entity.PremiumOption;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {

    private final ReservationRepository reservationRepository;
    private final SelectedPremiumOptionRepository selectedPremiumOptionRepository;
    private final RestaurantDomainService restaurantDomainService;

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public SelectedPremiumOption saveSelectedPremiumOption(Reservation reservation, Restaurant restaurant,
                                                           Long optionId) {
        PremiumOption option = restaurantDomainService.getOptionByRestaurantAndOptionId(restaurant, optionId);
        SelectedPremiumOption selectedOption = new SelectedPremiumOption(reservation, option);
        return selectedPremiumOptionRepository.save(selectedOption);
    }

    public List<Reservation> findAllByMemberId(Long memberId) {
        return reservationRepository.findAllByMemberId(memberId);
    }

    public Reservation getById(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new WellMeetDomainException(DomainErrorCode.RESERVATION_NOT_FOUND));
    }
    
    public Reservation getByIdAndMemberId(Long reservationId, Long memberId) {
        return reservationRepository.findByIdAndMemberId(reservationId, memberId)
                .orElseThrow(() -> new WellMeetDomainException(DomainErrorCode.UNAUTHORIZED_RESERVATION_ACCESS));
    }

    public List<SelectedPremiumOption> findAllSelectedOptionByReservationId(Long reservationId) {
        return selectedPremiumOptionRepository.findAllByReservationId(reservationId);
    }

    public List<Reservation> findAllByRestaurantId(String restaurantId) {
        return reservationRepository.findAllByRestaurantId(restaurantId);
    }

    public void deleteAllByReservationId(Long reservationId) {
        selectedPremiumOptionRepository.deleteAllByReservationId(reservationId);
    }

    public Page<Reservation> findByRestaurantIdWithFilters(String restaurantId, LocalDate date, 
                                                           ReservationStatus status, Pageable pageable) {
        return reservationRepository.findByRestaurantIdWithFilters(restaurantId, date, status, pageable);
    }

    public List<Reservation> findByRestaurantIdAndSearchQuery(String restaurantId, String query) {
        return reservationRepository.findByRestaurantIdAndSearchQuery(restaurantId, query);
    }

    public List<Reservation> findByRestaurantIdAndDateRange(String restaurantId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return reservationRepository.findByRestaurantIdAndReservationDateTimeBetween(restaurantId, startDateTime, endDateTime);
    }

    public List<Reservation> findByRestaurantIdAndMemberIdBeforeDate(String restaurantId, Long memberId, LocalDateTime beforeDate) {
        return reservationRepository.findByRestaurantIdAndMemberIdAndReservationDateTimeBefore(restaurantId, memberId, beforeDate);
    }
}
