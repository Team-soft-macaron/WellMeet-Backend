package com.wellmeet.domain.reservation;

import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.domain.reservation.dto.CreateReservationRequest;
import com.wellmeet.domain.reservation.dto.UpdateReservationRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationDomainController {

    private final ReservationApplicationService reservationApplicationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationDTO response = reservationApplicationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable Long id) {
        ReservationDTO response = reservationApplicationService.getReservation(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByRestaurant(
            @PathVariable String restaurantId
    ) {
        List<ReservationDTO> responses = reservationApplicationService
                .getReservationsByRestaurant(restaurantId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByMember(
            @PathVariable String memberId
    ) {
        List<ReservationDTO> responses = reservationApplicationService
                .getReservationsByMember(memberId);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReservationRequest request
    ) {
        ReservationDTO response = reservationApplicationService.updateReservation(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationApplicationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }
}
