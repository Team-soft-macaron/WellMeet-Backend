package com.wellmeet.reservation;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.entity.SelectedPremiumOption;
import com.wellmeet.domain.restaurant.RestaurantDomainService;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationDomainService reservationDomainService;
    private final RestaurantDomainService restaurantDomainService;
    private final MemberDomainService memberDomainService;

    @Transactional
    public CreateReservationResponse reserve(Long memberId, CreateReservationRequest request) {
        Restaurant restaurant = restaurantDomainService.getById(request.getRestaurantId());
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(request.getAvailableDateId(),
                restaurant);
        Member member = memberDomainService.getById(memberId);
        Reservation reservation = request.toDomain(restaurant, availableDate, member);

        Reservation savedReservation = reservationDomainService.save(reservation);
        List<String> optionNames = request.getSelectedPremiumOptionIds()
                .stream()
                .map(id -> reservationDomainService.saveSelectedPremiumOption(savedReservation, restaurant, id))
                .map(SelectedPremiumOption::getName)
                .toList();
        return new CreateReservationResponse(savedReservation, optionNames);
    }

    @Transactional(readOnly = true)
    public List<SummaryReservationResponse> getReservations(Long memberId) {
        return reservationDomainService.findAllByMemberId(memberId)
                .stream()
                .map(SummaryReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        double rating = restaurantDomainService.getAverageRating(reservation.getRestaurant().getId());
        List<String> selectedOptions = reservationDomainService.findAllSelectedOptionByReservationId(reservationId)
                .stream()
                .map(SelectedPremiumOption::getName)
                .toList();
        return new ReservationResponse(reservation, rating, selectedOptions);
    }

    @Transactional
    public CreateReservationResponse updateReservation(
            Long reservationId,
            Long memberId,
            CreateReservationRequest request
    ) {
        Restaurant restaurant = restaurantDomainService.getById(request.getRestaurantId());
        AvailableDate availableDate = restaurantDomainService.getAvailableDate(request.getAvailableDateId(),
                restaurant);
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        reservation.update(
                availableDate,
                request.getPurpose(),
                request.getPartySize(),
                request.getSpecialRequest()
        );
        reservationDomainService.deleteAllByReservationId(reservationId);
        List<String> optionNames = request.getSelectedPremiumOptionIds()
                .stream()
                .map(id -> reservationDomainService.saveSelectedPremiumOption(reservation, restaurant, id))
                .map(SelectedPremiumOption::getName)
                .toList();
        return new CreateReservationResponse(reservation, optionNames);
    }

    @Transactional
    public void cancel(Long reservationId, Long memberId) {
        Reservation reservation = reservationDomainService.getByIdAndMemberId(reservationId, memberId);
        reservation.cancel();
        reservationDomainService.save(reservation);
    }
}
