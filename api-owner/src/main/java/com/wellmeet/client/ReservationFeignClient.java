package com.wellmeet.client;

import com.wellmeet.common.dto.ReservationDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "domain-reservation-service")
public interface ReservationFeignClient {

    @GetMapping("/api/reservations/{id}")
    ReservationDTO getReservation(@PathVariable("id") Long id);

    @GetMapping("/api/reservations/restaurant/{restaurantId}")
    List<ReservationDTO> getReservationsByRestaurant(@PathVariable("restaurantId") String restaurantId);

    @PutMapping("/api/reservations/{id}/confirm")
    void confirmReservation(@PathVariable("id") Long id);
}
