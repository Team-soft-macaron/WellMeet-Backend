package com.wellmeet.reservation.dto;

import com.wellmeet.domain.member.entity.Member;
import com.wellmeet.domain.reservation.entity.Reservation;
import com.wellmeet.domain.restaurant.availabledate.entity.AvailableDate;
import com.wellmeet.domain.restaurant.entity.Restaurant;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReservationRequest {

    @NotNull
    String restaurantId;

    @NotNull
    Long availableDateId;

    int partySize;
    String specialRequest;

    public Reservation toDomain(Restaurant restaurant, AvailableDate availableDate, Member member) {
        return new Reservation(
                restaurant,
                availableDate,
                member,
                partySize,
                specialRequest
        );
    }
}
