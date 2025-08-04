package com.wellmeet.reservation;

import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationDomainService reservationDomainService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(String restaurantId) {
        return reservationDomainService.findAllByRestaurantId(restaurantId)
                .stream()
                .map(ReservationResponse::new)
                .toList();
    }
}
