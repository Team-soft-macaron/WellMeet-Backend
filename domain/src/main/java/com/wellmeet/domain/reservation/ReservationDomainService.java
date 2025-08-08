package com.wellmeet.domain.reservation;

import com.wellmeet.domain.common.RepositoryErrorDecoder;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.reservation.exception.ReservationErrorCode;
import com.wellmeet.domain.reservation.exception.ReservationException;
import com.wellmeet.domain.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationDomainService {

    private final ReservationRepository reservationRepository;

    public Reservation save(Reservation reservation) {
        try {
            return reservationRepository.save(reservation);
        } catch (DataIntegrityViolationException exception) {
            if (RepositoryErrorDecoder.isUniqueConstraintViolation(exception)) {
                throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
            }
            throw exception;
        }
    }

    public Reservation getByIdAndMemberId(Long reservationId, Long memberId) {
        return reservationRepository.findByIdAndMemberId(reservationId, memberId)
                .orElseThrow(() -> new ReservationException(ReservationErrorCode.UNAUTHORIZED_RESERVATION_ACCESS));
    }

    public List<Reservation> findAllByMemberId(Long memberId) {
        return reservationRepository.findAllByMemberId(memberId);
    }

    public List<Reservation> findAllByRestaurantId(String restaurantId) {
        return reservationRepository.findAllByRestaurantId(restaurantId);
    }

    public void alreadyReserved(Long memberId, String restaurantId, Long availableDateId) {
        if (reservationRepository.existsByMemberIdAndRestaurantIdAndAvailableDateId(
                memberId, restaurantId, availableDateId)) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }
    }
}
