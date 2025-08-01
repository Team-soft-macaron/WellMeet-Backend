package com.wellmeet.reservation;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.ReservationStatus;
import com.wellmeet.domain.reservation.entity.SelectedPremiumOption;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.reservation.dto.BookingDetailResponse;
import com.wellmeet.reservation.dto.BookingListResponse;
import com.wellmeet.reservation.dto.PagedBookingResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.UpdateBookingRequest;
import com.wellmeet.reservation.dto.UpdateBookingStatusRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OwnerReservationService {

    private final ReservationDomainService reservationDomainService;
    private final RestaurantDomainService restaurantDomainService;
    private final MemberDomainService memberDomainService;
    private final OwnerDomainService ownerDomainService;

    @Transactional(readOnly = true)
    public PagedBookingResponse getBookings(Long ownerId, LocalDate date, ReservationStatus status,
                                            int page, int limit) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        // 데이터베이스 레벨 페이징 및 필터링
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Reservation> reservationPage = reservationDomainService.findByRestaurantIdWithFilters(
                restaurantId, date, status, pageable);

        // N+1 문제 해결: Member 정보를 일괄 조회
        Set<Long> memberIds = reservationPage.getContent().stream()
                .map(Reservation::getMemberId)
                .collect(Collectors.toSet());
        
        Map<Long, Member> memberMap = memberDomainService.getAllByIds(memberIds);
        
        List<BookingListResponse> bookings = reservationPage.getContent().stream()
                .map(reservation -> convertToBookingListResponse(reservation, memberMap))
                .toList();

        return new PagedBookingResponse(bookings, reservationPage.getTotalElements(), 
                reservationPage.getNumber() + 1, reservationPage.getTotalPages());
    }

    @Transactional(readOnly = true)
    public BookingDetailResponse getBookingDetail(Long ownerId, Long bookingId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Reservation reservation = reservationDomainService.getById(bookingId);

        // 권한 체크: 해당 레스토랑의 예약인지 확인
        if (!reservation.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        return convertToBookingDetailResponse(reservation);
    }

    @Transactional
    public BookingDetailResponse updateBookingStatus(Long ownerId, Long bookingId,
                                                     UpdateBookingStatusRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Reservation reservation = reservationDomainService.getById(bookingId);

        // 권한 체크
        if (!reservation.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        // 상태 변경
        switch (request.getStatus()) {
            case CANCELED -> reservation.cancel();
            case CONFIRMED -> reservation.confirm();
            case COMPLETED -> reservation.complete();
            default -> reservation.updateStatus(request.getStatus());
        }

        Reservation savedReservation = reservationDomainService.save(reservation);
        return convertToBookingDetailResponse(savedReservation);
    }

    @Transactional
    public BookingDetailResponse updateBooking(Long ownerId, Long bookingId,
                                               UpdateBookingRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Reservation reservation = reservationDomainService.getById(bookingId);

        // 권한 체크
        if (!reservation.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        // 변경사항을 한 번에 적용
        LocalDateTime dateTime = request.getDateTime() != null ? 
                request.getDateTime() : reservation.getReservationDateTime();
        Integer partySize = request.getParty() != null ? 
                request.getParty() : reservation.getPartySize();
        String specialRequest = request.getNote() != null ? 
                request.getNote() : reservation.getSpecialRequest();
        
        reservation.update(dateTime, reservation.getPurpose(), partySize, specialRequest);

        // TODO: 테이블 번호 업데이트 로직 추가

        Reservation savedReservation = reservationDomainService.save(reservation);
        return convertToBookingDetailResponse(savedReservation);
    }

    @Transactional
    public void cancelBooking(Long ownerId, Long bookingId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Reservation reservation = reservationDomainService.getById(bookingId);

        // 권한 체크
        if (!reservation.getRestaurant().getId().equals(owner.getRestaurant().getId())) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        reservation.cancel();
        reservationDomainService.save(reservation);
    }

    @Transactional(readOnly = true)
    public List<BookingListResponse> searchBookings(Long ownerId, String query) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        // 데이터베이스 레벨 검색
        String searchQuery = "%" + query + "%";
        List<Reservation> searchResults = reservationDomainService.findByRestaurantIdAndSearchQuery(restaurantId, searchQuery);

        // N+1 문제 해결: Member 정보를 일괄 조회
        Set<Long> memberIds = searchResults.stream()
                .map(Reservation::getMemberId)
                .collect(Collectors.toSet());
        
        Map<Long, Member> memberMap = memberDomainService.getAllByIds(memberIds);
        
        return searchResults.stream()
                .map(reservation -> convertToBookingListResponse(reservation, memberMap))
                .toList();
    }

    private BookingListResponse convertToBookingListResponse(Reservation reservation) {
        Member member = memberDomainService.getById(reservation.getMemberId());
        BookingListResponse.CustomerSummary customer = new BookingListResponse.CustomerSummary(
                member.getId(),
                member.getName(),
                member.getEmail(), // 전화번호 대신 이메일 사용
                member.getEmail(),
                false // 현재는 VIP 필드가 없으므로 false로 설정
        );
        return new BookingListResponse(reservation, customer);
    }

    /**
     * N+1 문제 해결을 위한 오버로드된 converter 메서드
     * 미리 조회된 Member Map을 사용하여 추가 DB 조회를 방지
     */
    private BookingListResponse convertToBookingListResponse(Reservation reservation, Map<Long, Member> memberMap) {
        Member member = memberMap.get(reservation.getMemberId());
        if (member == null) {
            // 혹시 Map에 없는 경우 개별 조회 (방어 코드)
            member = memberDomainService.getById(reservation.getMemberId());
        }
        
        BookingListResponse.CustomerSummary customer = new BookingListResponse.CustomerSummary(
                member.getId(),
                member.getName(),
                member.getEmail(), // 전화번호 대신 이메일 사용
                member.getEmail(),
                false // 현재는 VIP 필드가 없으므로 false로 설정
        );
        return new BookingListResponse(reservation, customer);
    }

    private BookingDetailResponse convertToBookingDetailResponse(Reservation reservation) {
        Member member = memberDomainService.getById(reservation.getMemberId());

        BookingDetailResponse.CustomerDetail customer = new BookingDetailResponse.CustomerDetail(
                member.getId(),
                member.getName(),
                member.getEmail(), // 전화번호 대신 이메일 사용
                member.getEmail(),
                false, // 현재는 VIP 필드가 없으므로 false로 설정
                0, // 방문 횟수 계산 로직이 필요하나 현재는 0으로 설정
                null, // 마지막 방문일 데이터가 없으므로 null
                null, // 선호사항 필드가 없으므로 null
                null  // 알레르기 정보 필드가 없으므로 null
        );

        // TODO: 주문 내역 조회 로직 추가
        List<BookingDetailResponse.OrderItem> orderHistory = List.of();

        List<String> selectedOptions = reservationDomainService.findAllSelectedOptionByReservationId(
                        reservation.getId())
                .stream()
                .map(SelectedPremiumOption::getName)
                .toList();

        return new BookingDetailResponse(reservation, customer, orderHistory);
    }

    /**
     * N+1 문제 해결을 위한 오버로드된 converter 메서드 (Detail용)
     * 미리 조회된 Member Map을 사용하여 추가 DB 조회를 방지
     */
    private BookingDetailResponse convertToBookingDetailResponse(Reservation reservation, Map<Long, Member> memberMap) {
        Member member = memberMap.get(reservation.getMemberId());
        if (member == null) {
            // 혹시 Map에 없는 경우 개별 조회 (방어 코드)
            member = memberDomainService.getById(reservation.getMemberId());
        }

        BookingDetailResponse.CustomerDetail customer = new BookingDetailResponse.CustomerDetail(
                member.getId(),
                member.getName(),
                member.getEmail(), // 전화번호 대신 이메일 사용
                member.getEmail(),
                false, // 현재는 VIP 필드가 없으므로 false로 설정
                0, // 방문 횟수 계산 로직이 필요하나 현재는 0으로 설정
                null, // 마지막 방문일 데이터가 없으므로 null
                null, // 선호사항 필드가 없으므로 null
                null  // 알레르기 정보 필드가 없으므로 null
        );

        // TODO: 주문 내역 조회 로직 추가
        List<BookingDetailResponse.OrderItem> orderHistory = List.of();

        List<String> selectedOptions = reservationDomainService.findAllSelectedOptionByReservationId(
                        reservation.getId())
                .stream()
                .map(SelectedPremiumOption::getName)
                .toList();

        return new BookingDetailResponse(reservation, customer, orderHistory);
    }


}
