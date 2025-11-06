package com.wellmeet.client;

import com.wellmeet.client.dto.request.UpdateReservationDTO;
import com.wellmeet.common.dto.ReservationDTO;
import com.wellmeet.common.dto.request.CreateReservationDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "domain-reservation-service", path = "/api/reservation")
public interface ReservationFeignClient {

    @PostMapping
    ReservationDTO createReservation(@RequestBody CreateReservationDTO request);

    @GetMapping("/{id}")
    ReservationDTO getReservation(@PathVariable("id") Long id);

    @GetMapping("/restaurant/{restaurantId}")
    List<ReservationDTO> getReservationsByRestaurant(@PathVariable("restaurantId") String restaurantId);

    @GetMapping("/member/{memberId}")
    List<ReservationDTO> getReservationsByMember(@PathVariable("memberId") String memberId);

    @PutMapping("/{id}")
    ReservationDTO updateReservation(
            @PathVariable("id") Long id,
            @RequestBody UpdateReservationDTO request
    );

    @PatchMapping("/{id}/cancel")
    void cancelReservation(@PathVariable("id") Long id);
}
