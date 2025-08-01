package com.wellmeet.settings;

import com.wellmeet.settings.dto.BusinessSettingsResponse;
import com.wellmeet.settings.dto.SystemSettingsResponse;
import com.wellmeet.settings.dto.UpdateBusinessSettingsRequest;
import com.wellmeet.settings.dto.UpdateSystemSettingsRequest;
import com.wellmeet.settings.dto.BookingPolicyResponse;
import com.wellmeet.settings.dto.UpdateBookingPolicyRequest;
import com.wellmeet.settings.dto.AccessLogsResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
public class SettingsService {

    public BusinessSettingsResponse getBusinessSettings(Long ownerId) {
        return new BusinessSettingsResponse();
    }

    public BusinessSettingsResponse updateBusinessSettings(Long ownerId, UpdateBusinessSettingsRequest request) {
        return new BusinessSettingsResponse();
    }

    public SystemSettingsResponse getSystemSettings(Long ownerId) {
        return new SystemSettingsResponse();
    }

    public SystemSettingsResponse updateSystemSettings(Long ownerId, UpdateSystemSettingsRequest request) {
        return new SystemSettingsResponse();
    }

    public BookingPolicyResponse getBookingPolicy(Long ownerId) {
        BookingPolicyResponse.BookingPolicy policy = new BookingPolicyResponse.BookingPolicy(
            10,     // maxPartySize
            1,      // minPartySize  
            30,     // advanceBookingDays
            24,     // cancellationHours
            true,   // depositRequired
            new BigDecimal("50000"),  // depositAmount
            "예약 시 50% 선결제 필요",  // depositPolicy
            new BigDecimal("30000"),  // noShowPenalty
            true,   // specialRequestsAllowed
            30,     // bookingInterval
            50,     // maxBookingsPerDay
            Arrays.asList("2024-12-25", "2024-01-01")  // blackoutDates
        );
        
        return new BookingPolicyResponse(policy);
    }

    public BookingPolicyResponse updateBookingPolicy(Long ownerId, UpdateBookingPolicyRequest request) {
        BookingPolicyResponse.BookingPolicy policy = new BookingPolicyResponse.BookingPolicy(
            request.getMaxPartySize() != null ? request.getMaxPartySize() : 10,
            request.getMinPartySize() != null ? request.getMinPartySize() : 1,
            request.getAdvanceBookingDays() != null ? request.getAdvanceBookingDays() : 30,
            request.getCancellationHours() != null ? request.getCancellationHours() : 24,
            request.getDepositRequired() != null ? request.getDepositRequired() : true,
            request.getDepositAmount() != null ? request.getDepositAmount() : new BigDecimal("50000"),
            request.getDepositPolicy() != null ? request.getDepositPolicy() : "예약 시 50% 선결제 필요",
            request.getNoShowPenalty() != null ? request.getNoShowPenalty() : new BigDecimal("30000"),
            request.getSpecialRequestsAllowed() != null ? request.getSpecialRequestsAllowed() : true,
            request.getBookingInterval() != null ? request.getBookingInterval() : 30,
            request.getMaxBookingsPerDay() != null ? request.getMaxBookingsPerDay() : 50,
            request.getBlackoutDates() != null ? request.getBlackoutDates() : Arrays.asList("2024-12-25", "2024-01-01")
        );
        
        return new BookingPolicyResponse(policy);
    }

    public AccessLogsResponse getAccessLogs(Long ownerId, int page, int limit, Long userId, String action) {
        List<AccessLogsResponse.AccessLog> logs = Arrays.asList(
            new AccessLogsResponse.AccessLog(
                1L,
                1001L,
                "김철수",
                "예약 생성",
                "reservation",
                "테이블 2번 예약",
                "192.168.1.100",
                LocalDateTime.now().minusHours(2).toString()
            ),
            new AccessLogsResponse.AccessLog(
                2L,
                1002L, 
                "이영희",
                "예약 취소",
                "reservation",
                "테이블 3번 예약 취소",
                "192.168.1.101",
                LocalDateTime.now().minusHours(1).toString()
            ),
            new AccessLogsResponse.AccessLog(
                3L,
                1003L,
                "박민수",
                "메뉴 조회",
                "menu",
                "메뉴 리스트 조회",
                "192.168.1.102",
                LocalDateTime.now().minusMinutes(30).toString()
            )
        );

        return new AccessLogsResponse(logs, 3L, page, 1);
    }
}