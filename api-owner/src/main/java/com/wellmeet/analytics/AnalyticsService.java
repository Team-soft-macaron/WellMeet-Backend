package com.wellmeet.analytics;

import com.wellmeet.analytics.dto.CustomerTypeStatsResponse;
import com.wellmeet.analytics.dto.DashboardStatsResponse;
import com.wellmeet.analytics.dto.RevenueAnalysisResponse;
import com.wellmeet.analytics.dto.TimeSlotAnalysisResponse;
import com.wellmeet.analytics.dto.WeeklyAnalysisResponse;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final OwnerDomainService ownerDomainService;
    private final ReservationDomainService reservationDomainService;

    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(Long ownerId, LocalDate startDate, LocalDate endDate) {
        Owner owner = ownerDomainService.getById(ownerId);
        final String finalRestaurantId = owner.getRestaurant().getId();

        final LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().minusDays(30);
        final LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();

        List<Reservation> reservations = reservationDomainService.findByRestaurantIdAndDateRange(
                finalRestaurantId, finalStartDate.atStartOfDay(), finalEndDate.plusDays(1).atStartOfDay());

        int totalReservations = reservations.size();
        int confirmedReservations = (int) reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        int completedReservations = (int) reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .count();
        int cancelledReservations = (int) reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CANCELED)
                .count();

        BigDecimal totalRevenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

        BigDecimal averageOrderValue = completedReservations > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(completedReservations), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalCustomers = reservations.stream()
                .map(Reservation::getMemberId)
                .distinct()
                .count();

        long newCustomers = reservations.stream()
                .filter(r -> isNewCustomer(r.getMemberId(), finalRestaurantId, finalStartDate))
                .map(Reservation::getMemberId)
                .distinct()
                .count();

        long returningCustomers = totalCustomers - newCustomers;

        double occupancyRate = calculateOccupancyRate(finalRestaurantId, finalStartDate, finalEndDate);

        List<DashboardStatsResponse.RecentActivity> recentActivities = getRecentActivities(reservations);

        DashboardStatsResponse.Stats stats = new DashboardStatsResponse.Stats(
                totalReservations,
                confirmedReservations,
                completedReservations,
                cancelledReservations,
                totalRevenue,
                averageOrderValue,
                (int) totalCustomers,
                (int) newCustomers,
                (int) returningCustomers,
                occupancyRate,
                recentActivities
        );

        return new DashboardStatsResponse(stats);
    }

    @Transactional(readOnly = true)
    public WeeklyAnalysisResponse getWeeklyAnalysis(Long ownerId, LocalDate startDate) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        if (startDate == null) {
            startDate = LocalDate.now().minusWeeks(4);
        }

        LocalDate endDate = startDate.plusWeeks(4);
        
        List<Reservation> reservations = reservationDomainService.findByRestaurantIdAndDateRange(
                restaurantId, startDate.atStartOfDay(), endDate.atStartOfDay());

        Map<Integer, List<Reservation>> reservationsByWeek = reservations.stream()
                .collect(Collectors.groupingBy(r -> 
                    r.getReservationDateTime().toLocalDate().get(WeekFields.of(Locale.getDefault()).weekOfYear())));

        List<WeeklyAnalysisResponse.WeeklyData> weeklyDataList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            LocalDate weekStart = startDate.plusWeeks(i);
            LocalDate weekEnd = weekStart.plusDays(6);
            int weekOfYear = weekStart.get(WeekFields.of(Locale.getDefault()).weekOfYear());

            List<Reservation> weekReservations = reservationsByWeek.getOrDefault(weekOfYear, new ArrayList<>());

            int reservationCount = weekReservations.size();
            BigDecimal revenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

            long customerCount = weekReservations.stream()
                    .map(Reservation::getMemberId)
                    .distinct()
                    .count();

            double occupancyRate = calculateOccupancyRate(restaurantId, weekStart, weekEnd);

            List<WeeklyAnalysisResponse.DailyData> dailyBreakdown = createDailyBreakdown(weekReservations, weekStart);

            WeeklyAnalysisResponse.WeeklyData weeklyData = new WeeklyAnalysisResponse.WeeklyData(
                    weekStart.getYear() + "-W" + String.format("%02d", weekOfYear),
                    weekStart.toString(),
                    weekEnd.toString(),
                    reservationCount,
                    revenue,
                    (int) customerCount,
                    occupancyRate,
                    dailyBreakdown
            );

            weeklyDataList.add(weeklyData);
        }

        return new WeeklyAnalysisResponse(weeklyDataList);
    }

    @Transactional(readOnly = true)
    public CustomerTypeStatsResponse getCustomerTypeStats(Long ownerId, LocalDate startDate, LocalDate endDate) {
        Owner owner = ownerDomainService.getById(ownerId);
        final String finalRestaurantId = owner.getRestaurant().getId();

        final LocalDate finalStartDate = startDate != null ? startDate : LocalDate.now().minusDays(30);
        final LocalDate finalEndDate = endDate != null ? endDate : LocalDate.now();

        List<Reservation> reservations = reservationDomainService.findByRestaurantIdAndDateRange(
                finalRestaurantId, finalStartDate.atStartOfDay(), finalEndDate.plusDays(1).atStartOfDay());

        Map<Long, List<Reservation>> customerReservations = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getMemberId));

        List<CustomerTypeStatsResponse.CustomerTypeStats> customerTypes = new ArrayList<>();

        // 신규 고객 분석
        List<Long> newCustomers = customerReservations.entrySet().stream()
                .filter(entry -> isNewCustomer(entry.getKey(), finalRestaurantId, finalStartDate))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!newCustomers.isEmpty()) {
            customerTypes.add(createCustomerTypeStats("new", "신규 고객", 
                    newCustomers.stream().map(String::valueOf).collect(Collectors.toList()), customerReservations));
        }

        // 재방문 고객 분석
        List<Long> returningCustomers = customerReservations.entrySet().stream()
                .filter(entry -> !isNewCustomer(entry.getKey(), finalRestaurantId, finalStartDate))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!returningCustomers.isEmpty()) {
            customerTypes.add(createCustomerTypeStats("returning", "재방문 고객", 
                    returningCustomers.stream().map(String::valueOf).collect(Collectors.toList()), customerReservations));
        }

        return new CustomerTypeStatsResponse(customerTypes);
    }

    @Transactional(readOnly = true)
    public TimeSlotAnalysisResponse getTimeSlotAnalysis(Long ownerId, LocalDate startDate, LocalDate endDate) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Reservation> reservations = reservationDomainService.findByRestaurantIdAndDateRange(
                restaurantId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        Map<Integer, List<Reservation>> reservationsByHour = reservations.stream()
                .collect(Collectors.groupingBy(r -> r.getReservationDateTime().getHour()));

        List<TimeSlotAnalysisResponse.TimeSlotStats> timeSlots = new ArrayList<>();

        for (int hour = 9; hour <= 22; hour++) {
            List<Reservation> hourReservations = reservationsByHour.getOrDefault(hour, new ArrayList<>());

            String timeSlot = String.format("%02d:00-%02d:00", hour, hour + 1);
            int reservationCount = hourReservations.size();
            
            BigDecimal revenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

            double occupancyRate = calculateHourlyOccupancyRate(restaurantId, hour, startDate, endDate);
            
            int averagePartySize = hourReservations.isEmpty() ? 0 : 
                    hourReservations.stream().mapToInt(Reservation::getPartySize).sum() / hourReservations.size();

            double averageWaitTime = 0.0; // TODO: 실제 대기 시간 데이터가 있다면 계산

            List<TimeSlotAnalysisResponse.DayOfWeekData> dayBreakdown = createDayOfWeekBreakdown(hourReservations);

            TimeSlotAnalysisResponse.TimeSlotStats timeSlotStats = new TimeSlotAnalysisResponse.TimeSlotStats(
                    timeSlot,
                    hour,
                    reservationCount,
                    revenue,
                    occupancyRate,
                    averagePartySize,
                    averageWaitTime,
                    dayBreakdown
            );

            timeSlots.add(timeSlotStats);
        }

        return new TimeSlotAnalysisResponse(timeSlots);
    }

    @Transactional(readOnly = true)
    public RevenueAnalysisResponse getRevenueTrend(Long ownerId, LocalDate startDate, LocalDate endDate, String period) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(3);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        List<Reservation> reservations = reservationDomainService.findByRestaurantIdAndDateRange(
                restaurantId, startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay());

        List<Reservation> completedReservations = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMPLETED)
                .collect(Collectors.toList());

        BigDecimal totalRevenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

        List<RevenueAnalysisResponse.RevenueData> revenueData = createRevenueData(completedReservations, period, startDate, endDate);

        BigDecimal averageRevenue = revenueData.isEmpty() ? BigDecimal.ZERO :
                totalRevenue.divide(BigDecimal.valueOf(revenueData.size()), 2, RoundingMode.HALF_UP);

        double growthRate = calculateGrowthRate(revenueData);

        return new RevenueAnalysisResponse(period, totalRevenue, averageRevenue, growthRate, revenueData);
    }

    private boolean isNewCustomer(Long memberId, String restaurantId, LocalDate startDate) {
        // 시작 날짜 이전에 해당 고객의 예약이 있었는지 확인
        LocalDateTime beforeStartDate = startDate.atStartOfDay();
        List<Reservation> previousReservations = reservationDomainService.findByRestaurantIdAndMemberIdBeforeDate(
                restaurantId, memberId, beforeStartDate);
        return previousReservations.isEmpty();
    }

    private double calculateOccupancyRate(String restaurantId, LocalDate startDate, LocalDate endDate) {
        // 간단한 점유율 계산 로직
        // 실제로는 매장의 테이블 수, 시간대별 용량 등을 고려해야 함
        return Math.random() * 100; // TODO: 실제 점유율 계산 로직 구현
    }

    private double calculateHourlyOccupancyRate(String restaurantId, int hour, LocalDate startDate, LocalDate endDate) {
        // 시간대별 점유율 계산
        return Math.random() * 100; // TODO: 실제 시간대별 점유율 계산 로직 구현
    }

    private List<DashboardStatsResponse.RecentActivity> getRecentActivities(List<Reservation> reservations) {
        return reservations.stream()
                .sorted((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()))
                .limit(5)
                .map(r -> new DashboardStatsResponse.RecentActivity(
                        "reservation",
                        r.getStatus().name() + " 예약 - " + r.getPartySize() + "명",
                        r.getCreatedAt().toString()
                ))
                .collect(Collectors.toList());
    }

    private List<WeeklyAnalysisResponse.DailyData> createDailyBreakdown(List<Reservation> weekReservations, LocalDate weekStart) {
        Map<LocalDate, List<Reservation>> reservationsByDate = weekReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getReservationDateTime().toLocalDate()));

        List<WeeklyAnalysisResponse.DailyData> dailyData = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDate date = weekStart.plusDays(i);
            List<Reservation> dayReservations = reservationsByDate.getOrDefault(date, new ArrayList<>());

            BigDecimal revenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

            WeeklyAnalysisResponse.DailyData daily = new WeeklyAnalysisResponse.DailyData(
                    date.toString(),
                    date.getDayOfWeek().toString(),
                    dayReservations.size(),
                    revenue,
                    Math.random() * 100 // TODO: 실제 점유율 계산
            );

            dailyData.add(daily);
        }

        return dailyData;
    }

    private CustomerTypeStatsResponse.CustomerTypeStats createCustomerTypeStats(
            String type, String displayName, List<String> customerIds, Map<Long, List<Reservation>> customerReservations) {
        
        List<Reservation> typeReservations = customerIds.stream()
                .flatMap(id -> customerReservations.get(Long.valueOf(id)).stream())
                .collect(Collectors.toList());

        int count = customerIds.size();
        double percentage = 50.0; // TODO: 전체 고객 대비 비율 계산

        BigDecimal totalRevenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

        BigDecimal averageOrderValue = count > 0 
                ? totalRevenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int averageVisitCount = count > 0 ? typeReservations.size() / count : 0;

        CustomerTypeStatsResponse.CustomerBehavior behavior = new CustomerTypeStatsResponse.CustomerBehavior(
                "저녁 시간대", // TODO: 실제 선호 시간대 분석
                "금요일",     // TODO: 실제 선호 요일 분석
                2,           // TODO: 실제 평균 파티 크기 계산
                "스테이크",   // TODO: 실제 메뉴 분석
                5.0          // TODO: 실제 취소율 계산
        );

        return new CustomerTypeStatsResponse.CustomerTypeStats(
                type, displayName, count, percentage, totalRevenue, 
                averageOrderValue, averageVisitCount, behavior);
    }

    private List<TimeSlotAnalysisResponse.DayOfWeekData> createDayOfWeekBreakdown(List<Reservation> hourReservations) {
        Map<DayOfWeek, List<Reservation>> reservationsByDay = hourReservations.stream()
                .collect(Collectors.groupingBy(r -> r.getReservationDateTime().getDayOfWeek()));

        List<TimeSlotAnalysisResponse.DayOfWeekData> dayBreakdown = new ArrayList<>();

        for (DayOfWeek day : DayOfWeek.values()) {
            List<Reservation> dayReservations = reservationsByDay.getOrDefault(day, new ArrayList<>());
            
            TimeSlotAnalysisResponse.DayOfWeekData dayData = new TimeSlotAnalysisResponse.DayOfWeekData(
                    day.toString(),
                    dayReservations.size(),
                    Math.random() * 100 // TODO: 실제 점유율 계산
            );

            dayBreakdown.add(dayData);
        }

        return dayBreakdown;
    }

    private List<RevenueAnalysisResponse.RevenueData> createRevenueData(
            List<Reservation> completedReservations, String period, LocalDate startDate, LocalDate endDate) {
        
        List<RevenueAnalysisResponse.RevenueData> revenueData = new ArrayList<>();

        if ("monthly".equals(period)) {
            Map<String, List<Reservation>> reservationsByMonth = completedReservations.stream()
                    .collect(Collectors.groupingBy(r -> 
                        r.getReservationDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM"))));

            for (Map.Entry<String, List<Reservation>> entry : reservationsByMonth.entrySet()) {
                BigDecimal revenue = BigDecimal.ZERO; // TODO: Add totalAmount field to Reservation entity

                BigDecimal avgOrderValue = entry.getValue().isEmpty() ? BigDecimal.ZERO :
                        revenue.divide(BigDecimal.valueOf(entry.getValue().size()), 2, RoundingMode.HALF_UP);

                RevenueAnalysisResponse.RevenueBreakdown breakdown = new RevenueAnalysisResponse.RevenueBreakdown(
                        revenue.multiply(BigDecimal.valueOf(0.7)), // 음식 매출 (70%)
                        revenue.multiply(BigDecimal.valueOf(0.2)), // 음료 매출 (20%)
                        revenue.multiply(BigDecimal.valueOf(0.1)), // 서비스 매출 (10%)
                        BigDecimal.ZERO, // 할인
                        revenue.multiply(BigDecimal.valueOf(0.1))  // 세금 (10%)
                );

                RevenueAnalysisResponse.RevenueData data = new RevenueAnalysisResponse.RevenueData(
                        entry.getKey(),
                        entry.getKey() + "월",
                        revenue,
                        entry.getValue().size(),
                        avgOrderValue,
                        0.0, // TODO: 이전 기간 대비 성장률 계산
                        breakdown
                );

                revenueData.add(data);
            }
        }

        return revenueData;
    }

    private double calculateGrowthRate(List<RevenueAnalysisResponse.RevenueData> revenueData) {
        if (revenueData.size() < 2) {
            return 0.0;
        }

        BigDecimal firstPeriod = revenueData.get(0).getRevenue();
        BigDecimal lastPeriod = revenueData.get(revenueData.size() - 1).getRevenue();

        if (firstPeriod.equals(BigDecimal.ZERO)) {
            return 0.0;
        }

        return lastPeriod.subtract(firstPeriod)
                .divide(firstPeriod, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}