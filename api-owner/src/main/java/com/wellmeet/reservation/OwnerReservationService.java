package com.wellmeet.reservation;

import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.SelectedPremiumOption;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerReservationService {

    private final ReservationDomainService reservationDomainService;
    private final RestaurantDomainService restaurantDomainService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByRestaurant(String restaurantId, Long memberId) {
        return reservationDomainService.findAllByRestaurantId(restaurantId)
                .stream()
                .map(reservation -> getReservation(reservation.getId(), memberId))
                .toList();
    }

    private ReservationResponse getReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        double rating = restaurantDomainService.getAverageRating(reservation.getRestaurant().getId());
        List<String> selectedOptions = reservationDomainService.findAllSelectedOptionByReservationId(reservationId)
                .stream()
                .map(SelectedPremiumOption::getName)
                .toList();
        return new ReservationResponse(reservation, rating, selectedOptions);
    }
}
