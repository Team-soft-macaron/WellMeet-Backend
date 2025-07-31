package com.wellmeet.ownermanage;

import com.wellmeet.ownermanage.reservation.ReservationService;
import com.wellmeet.ownermanage.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;
    private final ReservationService reservationService;
    private final RestaurantService restaurantService;
}
