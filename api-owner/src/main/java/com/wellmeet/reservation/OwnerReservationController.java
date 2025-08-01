package com.wellmeet.reservation;

import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.reservation.dto.BookingDetailResponse;
import com.wellmeet.reservation.dto.BookingListResponse;
import com.wellmeet.reservation.dto.PagedBookingResponse;
import com.wellmeet.reservation.dto.UpdateBookingRequest;
import com.wellmeet.reservation.dto.UpdateBookingStatusRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/owner/bookings")
@RestController
@RequiredArgsConstructor
public class OwnerReservationController {

    private final OwnerReservationService ownerReservationService;

    @GetMapping
    public PagedBookingResponse getBookings(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ownerReservationService.getBookings(ownerId, date, status, page, limit);
    }

    @GetMapping("/{id}")
    public BookingDetailResponse getBookingDetail(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @PathVariable Long id
    ) {
        return ownerReservationService.getBookingDetail(ownerId, id);
    }

    @PatchMapping("/{id}/status")
    public BookingDetailResponse updateBookingStatus(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingStatusRequest request
    ) {
        return ownerReservationService.updateBookingStatus(ownerId, id, request);
    }

    @PatchMapping("/{id}")
    public BookingDetailResponse updateBooking(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @PathVariable Long id,
            @Valid @RequestBody UpdateBookingRequest request
    ) {
        return ownerReservationService.updateBooking(ownerId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelBooking(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @PathVariable Long id
    ) {
        ownerReservationService.cancelBooking(ownerId, id);
    }

    @GetMapping("/search")
    public List<BookingListResponse> searchBookings(
            @RequestParam Long ownerId, // TODO: JWT 인증에서 추출하도록 변경
            @RequestParam String q
    ) {
        return ownerReservationService.searchBookings(ownerId, q);
    }
}
