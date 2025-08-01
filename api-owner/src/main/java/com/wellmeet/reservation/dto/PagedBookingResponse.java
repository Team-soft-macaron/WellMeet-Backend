package com.wellmeet.reservation.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PagedBookingResponse {
    
    private List<BookingListResponse> bookings;
    private long total;
    private int page;
    private int totalPages;
}