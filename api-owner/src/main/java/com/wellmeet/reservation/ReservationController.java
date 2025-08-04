package com.wellmeet.reservation;

import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owner/reservation")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/{restaurantId}")
    public List<ReservationResponse> getReservations(
            @RequestParam(value = "ownerId") Long ownerId,
            @PathVariable String restaurantId
    ) {
        return reservationService.getReservations(restaurantId);
    }
}
