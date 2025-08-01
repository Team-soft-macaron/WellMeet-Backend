package com.wellmeet.analytics;

import com.wellmeet.analytics.dto.CustomerTypeStatsResponse;
import com.wellmeet.analytics.dto.DashboardStatsResponse;
import com.wellmeet.analytics.dto.RevenueAnalysisResponse;
import com.wellmeet.analytics.dto.TimeSlotAnalysisResponse;
import com.wellmeet.analytics.dto.WeeklyAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/owner/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    public DashboardStatsResponse getDashboardStats(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return analyticsService.getDashboardStats(ownerId, startDate, endDate);
    }

    @GetMapping("/weekly-data")
    public WeeklyAnalysisResponse getWeeklyAnalysis(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate
    ) {
        return analyticsService.getWeeklyAnalysis(ownerId, startDate);
    }

    @GetMapping("/customer-types")
    public CustomerTypeStatsResponse getCustomerTypeStats(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return analyticsService.getCustomerTypeStats(ownerId, startDate, endDate);
    }

    @GetMapping("/time-slots")
    public TimeSlotAnalysisResponse getTimeSlotAnalysis(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return analyticsService.getTimeSlotAnalysis(ownerId, startDate, endDate);
    }

    @GetMapping("/revenue-trend")
    public RevenueAnalysisResponse getRevenueTrend(
            @RequestParam Long ownerId, // TODO: JWT에서 추출
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(defaultValue = "monthly") String period
    ) {
        return analyticsService.getRevenueTrend(ownerId, startDate, endDate, period);
    }
}