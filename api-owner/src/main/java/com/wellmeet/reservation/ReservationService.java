package com.wellmeet.reservation;

import com.wellmeet.domain.member.MemberDomainService;
import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.ReservationDomainService;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.global.event.EventPublishService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationDomainService reservationDomainService;
    private final MemberDomainService memberDomainService;
    private final EventPublishService eventPublishService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(String restaurantId) {
        List<Reservation> reservations = reservationDomainService.findAllByRestaurantId(restaurantId);
        if (reservations.isEmpty()) {
            return List.of();
        }

        List<String> memberIds = reservations.stream()
                .map(Reservation::getMemberId)
                .distinct()
                .toList();
        Map<String, Member> membersById = memberDomainService.findAllByIds(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    Member member = membersById.get(reservation.getMemberId());
                    return new ReservationResponse(
                            reservation,
                            member.getName(),
                            member.getPhone(),
                            member.getEmail(),
                            member.isVip()
                    );
                })
                .toList();
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation reservation = reservationDomainService.getById(reservationId);
        reservation.confirm();

        Member member = memberDomainService.getById(reservation.getMemberId());
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(reservation, member.getName());
        eventPublishService.publishReservationConfirmedEvent(event);
    }
}
