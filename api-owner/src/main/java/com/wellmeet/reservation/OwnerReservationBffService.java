package com.wellmeet.reservation;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.common.dto.AvailableDateDTO;
import com.wellmeet.common.dto.MemberDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.RestaurantDTO;
import com.wellmeet.common.dto.request.MemberIdsRequest;
import com.wellmeet.global.event.OwnerEventPublishBffService;
import com.wellmeet.global.event.event.ReservationConfirmedEvent;
import com.wellmeet.reservation.dto.ReservationResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerReservationBffService {

    private final ReservationFeignClient reservationClient;
    private final MemberFeignClient memberClient;
    private final RestaurantFeignClient restaurantClient;
    private final OwnerEventPublishBffService eventPublishService;

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(String restaurantId) {
        List<ReservationDTO> reservations = reservationClient.getReservationsByRestaurant(restaurantId);
        if (reservations.isEmpty()) {
            return List.of();
        }

        List<String> memberIds = reservations.stream()
                .map(ReservationDTO::memberId)
                .distinct()
                .toList();
        Map<String, MemberDTO> membersById = memberClient.getMembersByIds(
                        new MemberIdsRequest(memberIds))
                .stream()
                .collect(Collectors.toMap(MemberDTO::id, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    MemberDTO member = membersById.get(reservation.memberId());
                    AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                            reservation.restaurantId(), reservation.availableDateId());
                    return new ReservationResponse(
                            reservation,
                            availableDate,
                            member.name(),
                            member.phone(),
                            member.email(),
                            member.isVip()
                    );
                })
                .toList();
    }

    @Transactional
    public void confirmReservation(Long reservationId) {
        reservationClient.confirmReservation(reservationId);

        ReservationDTO reservation = reservationClient.getReservation(reservationId);
        MemberDTO member = memberClient.getMember(reservation.memberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.restaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.restaurantId(), reservation.availableDateId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.date(), availableDate.time());
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                reservation, member.name(), restaurant.name(), dateTime);
        eventPublishService.publishReservationConfirmedEvent(event);
    }
}
