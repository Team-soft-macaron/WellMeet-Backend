package com.wellmeet.reservation;

import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/user/reservation")
@RestController
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public CreateReservationResponse reserve(
            @RequestParam(value = "memberId") Long memberId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationService.reserve(memberId, request);
    }

    @GetMapping
    public List<SummaryReservationResponse> getReservations(
            @RequestParam(value = "memberId") Long memberId
    ) {
        return reservationService.getReservations(memberId);
    }

    @GetMapping("/{reservationId}")
    public ReservationResponse getReservation(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable Long reservationId
    ) {
        return reservationService.getReservation(reservationId, memberId);
    }

    @GetMapping("/{restaurantId}")
    public List<ReservationResponse> getReservationsByRestaurant(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable String restaurantId
    ) {
        return reservationService.getReservationsByRestaurant(restaurantId, memberId);
    }

    @PutMapping("/{reservationId}")
    public CreateReservationResponse updateReservation(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable Long reservationId,
            @Valid @RequestBody CreateReservationRequest request
    ) {
        return reservationService.updateReservation(reservationId, memberId, request);
    }

    @DeleteMapping("/{reservationId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void cancelReservation(
            @RequestParam(value = "memberId") Long memberId,
            @PathVariable Long reservationId
    ) {
        reservationService.cancel(reservationId, memberId);
    }
}
