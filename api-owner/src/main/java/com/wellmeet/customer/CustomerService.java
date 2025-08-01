package com.wellmeet.customer;

import com.wellmeet.customer.dto.CreateCustomerRequest;
import com.wellmeet.customer.dto.CustomerBookingHistoryResponse;
import com.wellmeet.customer.dto.CustomerDetailResponse;
import com.wellmeet.customer.dto.CustomerListResponse;
import com.wellmeet.customer.dto.CustomerReviewHistoryResponse;
import com.wellmeet.customer.dto.PagedCustomerResponse;
import com.wellmeet.customer.dto.UpdateCustomerRequest;
import com.wellmeet.customer.dto.UpdateVipStatusRequest;
import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.owner.OwnerDomainService;
import com.wellmeet.domain.owner.entity.Owner;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.review.entity.Review;
import com.wellmeet.domain.restaurant.review.repository.ReviewRepository;
import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final MemberDomainService memberDomainService;
    private final OwnerDomainService ownerDomainService;
    private final ReservationDomainService reservationDomainService;
    private final ReviewRepository reviewRepository;

    @Transactional(readOnly = true)
    public PagedCustomerResponse getCustomers(Long ownerId, int page, int limit,
                                              String sort, Boolean isVip) {
        Owner owner = ownerDomainService.getById(ownerId);
        String restaurantId = owner.getRestaurant().getId();

        // 해당 레스토랑에 예약한 적이 있는 회원들 조회
        List<Reservation> allReservations = reservationDomainService.findAllByRestaurantId(restaurantId);
        List<Long> memberIds = allReservations.stream()
                .map(Reservation::getMemberId)
                .distinct()
                .toList();

        // N+1 문제 해결: Member 정보를 일괄 조회
        Set<Long> memberIdSet = memberIds.stream().collect(Collectors.toSet());
        Map<Long, Member> memberMap = memberDomainService.getAllByIds(memberIdSet);
        
        // TODO: 실제로는 페이징과 정렬이 가능한 쿼리로 개선 필요
        List<CustomerListResponse> customers = memberIds.stream()
                .map(memberId -> {
                    Member member = memberMap.get(memberId);
                    if (member == null) {
                        // 방어 코드: Map에 없는 경우 개별 조회
                        member = memberDomainService.getById(memberId);
                    }
                    return convertToCustomerListResponse(member, allReservations);
                })
                .filter(customer -> isVip == null || customer.isVip() == isVip)
                .toList();

        // 간단한 페이징 처리
        int start = (page - 1) * limit;
        int end = Math.min(start + limit, customers.size());
        List<CustomerListResponse> pagedCustomers = customers.subList(start, end);

        int totalPages = (int) Math.ceil((double) customers.size() / limit);

        return new PagedCustomerResponse(pagedCustomers, customers.size(), page, totalPages);
    }

    @Transactional(readOnly = true)
    public CustomerDetailResponse getCustomerDetail(Long ownerId, Long customerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        // 해당 레스토랑에 예약한 적이 있는지 확인
        List<Reservation> customerReservations = reservationDomainService.findAllByRestaurantId(
                        owner.getRestaurant().getId()).stream()
                .filter(r -> r.getMemberId().equals(customerId))
                .toList();

        if (customerReservations.isEmpty()) {
            throw new WellMeetException(ErrorCode.FORBIDDEN); // 해당 레스토랑 고객이 아님
        }

        CustomerDetailResponse.CustomerDetail customerDetail = convertToCustomerDetail(member, customerReservations);
        return new CustomerDetailResponse(customerDetail);
    }

    @Transactional
    public CustomerDetailResponse createCustomer(Long ownerId, CreateCustomerRequest request) {
        // TODO: 실제로는 Member 엔티티를 생성해야 하지만, 
        // 현재 Member는 사용자가 가입할 때 생성되는 구조로 보임
        // 사장님이 직접 고객을 등록하는 기능은 별도의 Customer 엔티티가 필요할 수 있음

        throw new WellMeetException(ErrorCode.INTERNAL_SERVER_ERROR); // 임시
    }

    @Transactional
    public CustomerDetailResponse updateCustomer(Long ownerId, Long customerId, UpdateCustomerRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        // 해당 레스토랑에 예약한 적이 있는지 확인
        List<Reservation> customerReservations = reservationDomainService.findAllByRestaurantId(
                        owner.getRestaurant().getId()).stream()
                .filter(r -> r.getMemberId().equals(customerId))
                .toList();

        if (customerReservations.isEmpty()) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        // TODO: Member 엔티티에 고객 정보 업데이트 메서드 추가 필요
        // member.updateCustomerInfo(request);

        CustomerDetailResponse.CustomerDetail customerDetail = convertToCustomerDetail(member, customerReservations);
        return new CustomerDetailResponse(customerDetail);
    }

    @Transactional
    public void deleteCustomer(Long ownerId, Long customerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        // 해당 레스토랑에 예약한 적이 있는지 확인
        List<Reservation> customerReservations = reservationDomainService.findAllByRestaurantId(
                        owner.getRestaurant().getId()).stream()
                .filter(r -> r.getMemberId().equals(customerId))
                .toList();

        if (customerReservations.isEmpty()) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        // TODO: 실제로는 Member를 완전 삭제하는 것이 아니라 
        // 해당 레스토랑에서의 관계만 삭제하는 것이 맞을 수 있음
        // 현재는 Member를 완전 삭제하지 않고 예외 발생
        throw new WellMeetException(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    @Transactional(readOnly = true)
    public CustomerBookingHistoryResponse getCustomerBookings(Long ownerId, Long customerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        List<Reservation> customerReservations = reservationDomainService.findAllByRestaurantId(
                        owner.getRestaurant().getId()).stream()
                .filter(r -> r.getMemberId().equals(customerId))
                .toList();

        if (customerReservations.isEmpty()) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        List<CustomerBookingHistoryResponse.BookingHistory> bookingHistory = customerReservations.stream()
                .map(reservation -> new CustomerBookingHistoryResponse.BookingHistory(
                        reservation.getId(),
                        reservation.getReservationDateTime().toLocalDate().toString(),
                        reservation.getReservationDateTime().toLocalTime().toString(),
                        reservation.getPartySize(),
                        reservation.getStatus(),
                        null, // TODO: 테이블 번호
                        BigDecimal.valueOf(50000), // TODO: 실제 주문 금액
                        reservation.getCreatedAt()
                ))
                .toList();

        return new CustomerBookingHistoryResponse(bookingHistory);
    }

    @Transactional(readOnly = true)
    public CustomerReviewHistoryResponse getCustomerReviews(Long ownerId, Long customerId) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        // 해당 레스토랑에 대한 고객의 리뷰 조회
        List<Review> customerReviews = reviewRepository.findByRestaurantId(owner.getRestaurant().getId())
                .stream()
                .filter(review -> review.getMember().getId().equals(customerId))
                .toList();

        List<CustomerReviewHistoryResponse.ReviewHistory> reviewHistory = customerReviews.stream()
                .map(review -> new CustomerReviewHistoryResponse.ReviewHistory(
                        review.getId(),
                        review.getRating(),
                        review.getContent(),
                        null, // TODO: 답글 기능 구현 후
                        review.getCreatedAt(),
                        null // TODO: 예약 ID 연결
                ))
                .toList();

        return new CustomerReviewHistoryResponse(reviewHistory);
    }

    @Transactional
    public CustomerDetailResponse updateVipStatus(Long ownerId, Long customerId, UpdateVipStatusRequest request) {
        Owner owner = ownerDomainService.getById(ownerId);
        Member member = memberDomainService.getById(customerId);

        // 해당 레스토랑에 예약한 적이 있는지 확인
        List<Reservation> customerReservations = reservationDomainService.findAllByRestaurantId(
                        owner.getRestaurant().getId()).stream()
                .filter(r -> r.getMemberId().equals(customerId))
                .toList();

        if (customerReservations.isEmpty()) {
            throw new WellMeetException(ErrorCode.FORBIDDEN);
        }

        // TODO: Member 엔티티에 VIP 상태 업데이트 메서드 추가 필요
        // 또는 별도의 CustomerVip 엔티티 생성
        // member.updateVipStatus(request.getIsVip());

        CustomerDetailResponse.CustomerDetail customerDetail = convertToCustomerDetail(member, customerReservations);
        return new CustomerDetailResponse(customerDetail);
    }

    private CustomerListResponse convertToCustomerListResponse(Member member, List<Reservation> allReservations) {
        List<Reservation> memberReservations = allReservations.stream()
                .filter(r -> r.getMemberId().equals(member.getId()))
                .toList();

        int visitCount = memberReservations.size();
        LocalDateTime lastVisit = memberReservations.stream()
                .map(Reservation::getReservationDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // TODO: 실제 총 사용금액 계산
        BigDecimal totalSpent = BigDecimal.valueOf(visitCount * 50000L);

        double averagePartySize = memberReservations.stream()
                .mapToInt(Reservation::getPartySize)
                .average()
                .orElse(0.0);

        // TODO: VIP 여부 판단 로직
        boolean isVip = visitCount >= 10 || totalSpent.compareTo(BigDecimal.valueOf(500000)) >= 0;

        return new CustomerListResponse(
                member.getId(),
                member.getName(),
                "010-0000-0000", // TODO: Member에 전화번호 필드 추가
                member.getEmail(),
                isVip,
                visitCount,
                lastVisit,
                totalSpent,
                averagePartySize,
                List.of(), // TODO: 태그 기능
                member.getCreatedAt()
        );
    }

    private CustomerDetailResponse.CustomerDetail convertToCustomerDetail(Member member,
                                                                          List<Reservation> reservations) {
        CustomerListResponse listResponse = convertToCustomerListResponse(member, reservations);

        return new CustomerDetailResponse.CustomerDetail(
                member.getId(),
                member.getName(),
                "010-0000-0000", // TODO: 전화번호
                member.getEmail(),
                listResponse.isVip(),
                listResponse.getVisitCount(),
                listResponse.getLastVisit(),
                listResponse.getTotalSpent(),
                listResponse.getAveragePartySize(),
                null, // TODO: 선호사항
                null, // TODO: 알레르기
                null, // TODO: 생일
                null, // TODO: 메모
                List.of(), // TODO: 태그
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
