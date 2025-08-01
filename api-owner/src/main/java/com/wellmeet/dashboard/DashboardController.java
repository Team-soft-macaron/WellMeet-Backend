package com.wellmeet.dashboard;

import com.wellmeet.dashboard.dto.KpiResponse;
import com.wellmeet.dashboard.dto.RecentBookingResponse;
import com.wellmeet.dashboard.dto.TimeSlotResponse;
import com.wellmeet.dashboard.dto.TodayStatsResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/today-stats")
    public TodayStatsResponse getTodayStats(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return dashboardService.getTodayStats(ownerId);
    }

    @GetMapping("/recent-bookings")
    public List<RecentBookingResponse> getRecentBookings(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(defaultValue = "10") int limit
    ) {
        return dashboardService.getRecentBookings(ownerId, Math.min(limit, 50));
    }

    @GetMapping("/kpi")
    public KpiResponse getKpi(@RequestParam Long ownerId) { // TODO: JWT에서 추출
        return dashboardService.getKpi(ownerId);
    }

    @GetMapping("/time-slots")
    public List<TimeSlotResponse> getTimeSlots(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return dashboardService.getTimeSlots(ownerId, date);
    }
}
