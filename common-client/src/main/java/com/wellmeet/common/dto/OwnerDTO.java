package com.wellmeet.common.dto;

public record OwnerDTO(
        String id,
        String name,
        String email,
        boolean reservationEnabled,
        boolean reviewEnabled
) {
}
