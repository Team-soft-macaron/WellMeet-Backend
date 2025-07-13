package com.wellmeet.reservation;

import com.wellmeet.exception.ErrorCode;
import com.wellmeet.exception.WellMeetException;
import com.wellmeet.reservation.domain.Reservation;
import com.wellmeet.reservation.domain.SelectedPremiumOption;
import com.wellmeet.reservation.dto.CreateReservationRequest;
import com.wellmeet.reservation.dto.CreateReservationResponse;
import com.wellmeet.reservation.dto.ReservationResponse;
import com.wellmeet.reservation.dto.SummaryReservationResponse;
import com.wellmeet.reservation.repository.ReservationRepository;
import com.wellmeet.reservation.repository.SelectedPremiumOptionRepository;
import com.wellmeet.restaurant.RestaurantService;
import com.wellmeet.restaurant.domain.PremiumOption;
import com.wellmeet.restaurant.domain.Restaurant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SelectedPremiumOptionRepository selectedPremiumOptionRepository;
    private final RestaurantService restaurantService;

    @Transactional
    public CreateReservationResponse reserve(Long memberId, CreateReservationRequest request) {
        Restaurant restaurant = restaurantService.getById(request.getRestaurantId());
        Reservation reservation = new Reservation(
                request.getDateTime(),
                request.getPurpose(),
                restaurant,
                memberId,
                request.getPartySize(),
                request.getSpecialRequest()
        );
        Reservation savedReservation = reservationRepository.save(reservation);
        List<String> optionNames = request.getSelectedPremiumOptionIds()
                .stream()
                .map(id -> saveSelectedPremiumOption(savedReservation, restaurant, id))
                .map(SelectedPremiumOption::getName)
                .toList();
        return new CreateReservationResponse(savedReservation, optionNames);
    }

    private SelectedPremiumOption saveSelectedPremiumOption(Reservation reservation, Restaurant restaurant,
                                                            Long optionId) {
        PremiumOption option = restaurantService.getOptionByRestaurantAndOptionId(restaurant, optionId);
        SelectedPremiumOption selectedOption = new SelectedPremiumOption(reservation, option);
        return selectedPremiumOptionRepository.save(selectedOption);
    }

    @Transactional
    public void cancel(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findByIdAndMemberId(reservationId, memberId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.UNAUTHORIZED_RESERVATION_ACCESS));
        reservation.cancel();
        reservationRepository.save(reservation);
    }

    @Transactional
    public CreateReservationResponse updateReservation(
            Long reservationId,
            Long memberId,
            CreateReservationRequest request
    ) {
        Restaurant restaurant = restaurantService.getById(request.getRestaurantId());
        Reservation reservation = reservationRepository.findByIdAndMemberId(reservationId, memberId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.UNAUTHORIZED_RESERVATION_ACCESS));
        reservation.update(
                request.getDateTime(),
                request.getPurpose(),
                request.getPartySize(),
                request.getSpecialRequest()
        );
        selectedPremiumOptionRepository.deleteAllByReservationId(reservationId);
        List<String> optionNames = request.getSelectedPremiumOptionIds()
                .stream()
                .map(id -> saveSelectedPremiumOption(reservation, restaurant, id))
                .map(SelectedPremiumOption::getName)
                .toList();
        return new CreateReservationResponse(reservation, optionNames);
    }

    @Transactional(readOnly = true)
    public List<SummaryReservationResponse> getReservations(Long memberId) {
        return reservationRepository.findAllByMemberId(memberId)
                .stream()
                .map(SummaryReservationResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long reservationId, Long memberId) {
        Reservation reservation = reservationRepository.findByIdAndMemberId(reservationId, memberId)
                .orElseThrow(() -> new WellMeetException(ErrorCode.UNAUTHORIZED_RESERVATION_ACCESS));
        double rating = restaurantService.getAverageRating(reservation.getRestaurant().getId());
        List<String> selectedOptions = selectedPremiumOptionRepository.findAllByReservationId(reservationId)
                .stream()
                .map(SelectedPremiumOption::getName)
                .toList();
        return new ReservationResponse(reservation, rating, selectedOptions);
    }
}
