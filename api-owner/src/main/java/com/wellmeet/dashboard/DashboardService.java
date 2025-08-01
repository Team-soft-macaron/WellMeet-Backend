package com.wellmeet.dashboard;

import com.wellmeet.dashboard.dto.KpiResponse;
import com.wellmeet.dashboard.dto.RecentBookingResponse;
import com.wellmeet.dashboard.dto.TimeSlotResponse;
import com.wellmeet.dashboard.dto.TodayStatsResponse;
import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ReservationDomainService reservationDomainService;
    private final OwnerDomainService ownerDomainService;
    private final MemberDomainService memberDomainService;
    private final RestaurantDomainService restaurantDomainService;

    @Transactional(readOnly = true)
    public TodayStatsResponse getTodayStats(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        List<Reservation> allReservations = reservationDomainService.findAllByRestaurantId(restaurantId);
        LocalDate today = LocalDate.now();

        List<Reservation> todayReservations = allReservations.stream()
                .filter(r -> r.getReservationDateTime().toLocalDate().equals(today))
                .toList();

        int todayBookings = todayReservations.size();
        int confirmedBookings = (int) todayReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
                .count();
        int pendingBookings = (int) todayReservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.PENDING)
                .count();

        // TODO: 예상 매출 계산 로직 추가 (메뉴 가격 * 인원수 등)
        long expectedRevenue = todayReservations.stream()
                .mapToLong(r -> r.getPartySize() * 30000L) // 임시로 1인당 3만원으로 계산
                .sum();

        return new TodayStatsResponse(todayBookings, confirmedBookings, pendingBookings, expectedRevenue);
    }

    @Transactional(readOnly = true)
    public List<RecentBookingResponse> getRecentBookings(Long ownerId, int limit) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        List<Reservation> allReservations = reservationDomainService.findAllByRestaurantId(restaurantId);

        // 최근 예약 순으로 정렬하고 limit만큼 가져오기
        return allReservations.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .map(this::convertToRecentBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public KpiResponse getKpi(Long ownerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        // TODO: 실제 KPI 계산 로직 구현
        // 이번 달과 지난 달 데이터를 비교하여 변화율 계산

        KpiResponse.KpiMetric bookingCount = new KpiResponse.KpiMetric(150, 12.5);
        KpiResponse.KpiMetric revenue = new KpiResponse.KpiMetric(4500000, 8.3);
        KpiResponse.KpiMetric avgPartySize = new KpiResponse.KpiMetric(3.2, -2.1);
        KpiResponse.KpiMetric satisfactionScore = new KpiResponse.KpiMetric(4.5, 0.2);

        return new KpiResponse(bookingCount, revenue, avgPartySize, satisfactionScore);
    }

    @Transactional(readOnly = true)
    public List<TimeSlotResponse> getTimeSlots(Long ownerId, LocalDate date) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        List<Reservation> allReservations = reservationDomainService.findAllByRestaurantId(restaurantId);

        // 해당 날짜의 예약만 필터링
        List<Reservation> dateReservations = allReservations.stream()
                .filter(r -> r.getReservationDateTime().toLocalDate().equals(date))
                .toList();

        // 11:00부터 21:00까지 30분 단위로 시간대 생성
        List<TimeSlotResponse> timeSlots = new ArrayList<>();
        LocalTime startTime = LocalTime.of(11, 0);
        LocalTime endTime = LocalTime.of(21, 0);

        while (!startTime.isAfter(endTime)) {
            LocalTime slotTime = startTime;
            int reservations = (int) dateReservations.stream()
                    .filter(r -> {
                        LocalTime resTime = r.getReservationDateTime().toLocalTime();
                        return resTime.equals(slotTime) ||
                                (resTime.isAfter(slotTime) && resTime.isBefore(slotTime.plusMinutes(30)));
                    })
                    .count();

            // TODO: 실제 capacity는 레스토랑 정보에서 가져와야 함
            int capacity = 20;
            boolean available = reservations < capacity;

            timeSlots.add(new TimeSlotResponse(
                    slotTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    reservations,
                    capacity,
                    available
            ));

            startTime = startTime.plusMinutes(30);
        }

        return timeSlots;
    }

    private RecentBookingResponse convertToRecentBookingResponse(Reservation reservation) {
        Member member = memberDomainService.getById(reservation.getMemberId());

        RecentBookingResponse.CustomerInfo customerInfo = new RecentBookingResponse.CustomerInfo(
                member.getId(),
                member.getName(),
                "010-0000-0000", // TODO: 전화번호 필드 추가
                false // TODO: VIP 여부 판단 로직
        );

        return new RecentBookingResponse(
                reservation.getId(),
                customerInfo,
                reservation.getReservationDateTime().toString(),
                reservation.getPartySize(),
                reservation.getStatus(),
                reservation.getSpecialRequest(),
                null // TODO: 테이블 번호
        );
    }
}
