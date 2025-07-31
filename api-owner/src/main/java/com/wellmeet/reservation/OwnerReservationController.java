package com.wellmeet.reservation;

import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owner/reservations")
@RestController
@RequiredArgsConstructor
public class OwnerReservationController {

    private final OwnerReservationService ownerReservationService;

    @GetMapping("/{restaurantId}")
    public List<ReservationResponse> getReservationsByRestaurant(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable String restaurantId
    ) {
        return ownerReservationService.getReservationsByRestaurant(restaurantId, memberId);
    }
}
