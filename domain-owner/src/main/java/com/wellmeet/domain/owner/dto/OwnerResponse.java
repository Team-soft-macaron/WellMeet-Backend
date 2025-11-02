package com.wellmeet.domain.owner.dto;

import com.wellmeet.domain.owner.entity.Owner;

public record OwnerResponse(
        String id,
        String name,
        String email,
        boolean reservationEnabled,
        boolean reviewEnabled
) {
    public static OwnerResponse from(Owner owner) {
        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getEmail(),
                owner.isReservationEnabled(),
                owner.isReviewEnabled()
        );
    }
}
