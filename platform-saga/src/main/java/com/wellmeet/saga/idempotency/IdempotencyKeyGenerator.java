package com.wellmeet.saga.idempotency;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.stream.Collectors;

@UtilityClass
public class IdempotencyKeyGenerator {

    public String generate(String... parts) {
        return Arrays.stream(parts)
            .filter(part -> part != null && !part.isBlank())
            .collect(Collectors.joining(":"));
    }

    public String forReservationCreate(String memberId, String restaurantId, Long availableDateId) {
        return generate("member", memberId, "restaurant", restaurantId, "date", String.valueOf(availableDateId));
    }

    public String forReservationUpdate(Long reservationId) {
        return generate("reservation", String.valueOf(reservationId), "update");
    }

    public String forReservationCancel(Long reservationId) {
        return generate("reservation", String.valueOf(reservationId), "cancel");
    }

    public String forFavoriteAdd(String memberId, String restaurantId) {
        return generate("member", memberId, "favorite", restaurantId);
    }

    public String forFavoriteRemove(String memberId, String restaurantId) {
        return generate("member", memberId, "unfavorite", restaurantId);
    }

    public String forReservationConfirm(Long reservationId) {
        return generate("reservation", String.valueOf(reservationId), "confirm");
    }

    public String forOperatingHoursUpdate(String restaurantId, String timestamp) {
        return generate("restaurant", restaurantId, "hours", timestamp);
    }

    public String forRestaurantUpdate(String restaurantId, String timestamp) {
        return generate("restaurant", restaurantId, "update", timestamp);
    }
}
