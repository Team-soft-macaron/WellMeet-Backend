package com.wellmeet.reservation;

import com.wellmeet.client.MemberFeignClient;
import com.wellmeet.client.ReservationFeignClient;
import com.wellmeet.client.RestaurantFeignClient;
import com.wellmeet.client.dto.AvailableDateDTO;
import com.wellmeet.client.dto.MemberDTO;
import com.wellmeet.client.dto.ReservationDTO;
import com.wellmeet.client.dto.RestaurantDTO;
import com.wellmeet.client.dto.request.MemberIdsRequest;
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
                .map(ReservationDTO::getMemberId)
                .distinct()
                .toList();
        Map<String, MemberDTO> membersById = memberClient.getMembersByIds(
                        MemberIdsRequest.builder().memberIds(memberIds).build())
                .stream()
                .collect(Collectors.toMap(MemberDTO::getId, Function.identity()));

        return reservations.stream()
                .map(reservation -> {
                    MemberDTO member = membersById.get(reservation.getMemberId());
                    AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                            reservation.getRestaurantId(), reservation.getAvailableDateId());
                    return new ReservationResponse(
                            reservation,
                            availableDate,
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
        reservationClient.confirmReservation(reservationId);

        ReservationDTO reservation = reservationClient.getReservation(reservationId);
        MemberDTO member = memberClient.getMember(reservation.getMemberId());
        RestaurantDTO restaurant = restaurantClient.getRestaurant(reservation.getRestaurantId());
        AvailableDateDTO availableDate = restaurantClient.getAvailableDate(
                reservation.getRestaurantId(), reservation.getAvailableDateId());
        LocalDateTime dateTime = LocalDateTime.of(availableDate.getDate(), availableDate.getTime());
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(
                reservation, member.getName(), restaurant.getName(), dateTime);
        eventPublishService.publishReservationConfirmedEvent(event);
    }
}
